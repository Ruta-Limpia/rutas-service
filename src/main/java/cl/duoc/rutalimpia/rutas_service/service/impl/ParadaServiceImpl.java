package cl.duoc.rutalimpia.rutas_service.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cl.duoc.rutalimpia.rutas_service.exception.ReglaNegocioException;

import cl.duoc.rutalimpia.rutas_service.client.SolicitudesClient;
import cl.duoc.rutalimpia.rutas_service.dto.ActualizarEstadoSolicitudRequest;
import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoSolicitud;

import cl.duoc.rutalimpia.rutas_service.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.rutalimpia.rutas_service.dto.AgregarParadaRequest;
import cl.duoc.rutalimpia.rutas_service.dto.ParadaResponse;
import cl.duoc.rutalimpia.rutas_service.dto.RegistrarResultadoRequest;

import cl.duoc.rutalimpia.rutas_service.mapper.ParadaMapper;

import cl.duoc.rutalimpia.rutas_service.model.HojaRuta;
import cl.duoc.rutalimpia.rutas_service.model.Parada;

import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoHojaRuta;
import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoParada;

import cl.duoc.rutalimpia.rutas_service.repository.HojaRutaRepository;
import cl.duoc.rutalimpia.rutas_service.repository.ParadaRepository;

import cl.duoc.rutalimpia.rutas_service.service.ParadaService;

@Service
public class ParadaServiceImpl implements ParadaService {

    private static final Logger logger =
            LoggerFactory.getLogger(ParadaServiceImpl.class);

    private final ParadaRepository paradaRepository;
    private final HojaRutaRepository hojaRutaRepository;
    private final ParadaMapper paradaMapper;
    private final SolicitudesClient solicitudesClient;
    

    public ParadaServiceImpl(
            ParadaRepository paradaRepository,
            HojaRutaRepository hojaRutaRepository,
            ParadaMapper paradaMapper,
            SolicitudesClient solicitudesClient
    ) {
        this.paradaRepository = paradaRepository;
        this.hojaRutaRepository = hojaRutaRepository;
        this.paradaMapper = paradaMapper;
        this.solicitudesClient = solicitudesClient;
    }

    @Override
    @Transactional
    public ParadaResponse agregarParada(AgregarParadaRequest request) {

        // Verificar si la solicitud ya tiene una parada
        var paradaExistente = paradaRepository
                .findBySolicitudId(request.solicitudId());

        if (paradaExistente.isPresent()) {
            return paradaMapper.toResponse(paradaExistente.get());
        }

        // Buscar la hoja de ruta del camión para la fecha
        HojaRuta hojaRuta = hojaRutaRepository
                .findByCamionIdAndFecha(
                        request.camionId(),
                        request.fecha()
                )
                .orElseGet(() -> {

                    HojaRuta nuevaHoja = new HojaRuta();

                    nuevaHoja.setCamionId(request.camionId());
                    nuevaHoja.setConductorId(request.conductorId());
                    nuevaHoja.setFecha(request.fecha());
                    nuevaHoja.setEstado(EstadoHojaRuta.ABIERTA);

                    return hojaRutaRepository.save(nuevaHoja);
                });

        // Comprobar que la hoja siga abierta
        if (hojaRuta.getEstado() != EstadoHojaRuta.ABIERTA) {
            throw new ReglaNegocioException(
                    "La hoja de ruta ya está finalizada"
            );
        }

        // Verificar la capacidad del camión
        long cantidadParadas = paradaRepository
                .countByHojaRutaId(hojaRuta.getId());

        if (cantidadParadas >= request.capacidadParadas()) {
            throw new ReglaNegocioException(
                    "Camión sin cupo"
            );
        }

        // Crear la nueva parada
        Parada parada = new Parada();

        parada.setHojaRuta(hojaRuta);
        parada.setSolicitudId(request.solicitudId());
        parada.setFolio(request.folio());
        parada.setDireccion(request.direccion());
        parada.setComuna(request.comuna());
        parada.setTipoResiduo(request.tipoResiduo());
        parada.setBloqueHorario(request.bloqueHorario());

        parada.setOrden((int) cantidadParadas + 1);
        parada.setEstado(EstadoParada.PENDIENTE);

        // Guardar la parada
        Parada paradaGuardada = paradaRepository.save(parada);

        // Devolver el DTO
        return paradaMapper.toResponse(paradaGuardada);
    }

    @Override
    @Transactional
    public ParadaResponse registrarResultado(
            Long paradaId,
            RegistrarResultadoRequest request,
            Long conductorId
    ) {

        // Buscar la parada
        Parada parada = paradaRepository.findById(paradaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la parada"
                        )
                );

        // Verificar que pertenezca al conductor
        if (!parada.getHojaRuta()
                .getConductorId()
                .equals(conductorId)) {

            throw new RecursoNoEncontradoException(
                    "No se encontró la parada para este conductor"
            );
        }

        // Verificar que esté pendiente
        if (parada.getEstado() != EstadoParada.PENDIENTE) {
            throw new ReglaNegocioException(
                    "La parada ya tiene un resultado registrado"
            );
        }

        // Validar el resultado
        if (request.resultado() != EstadoParada.REALIZADA
                && request.resultado() != EstadoParada.FALLIDA) {

            throw new ReglaNegocioException(
                    "El resultado debe ser REALIZADA o FALLIDA"
            );
        }

        // Exigir motivo cuando el retiro falle
        if (request.resultado() == EstadoParada.FALLIDA
                && (request.motivoFallo() == null
                || request.motivoFallo().isBlank())) {

            throw new ReglaNegocioException(
                    "Debes indicar el motivo"
            );
        }

        // Actualizar el resultado
        parada.setEstado(request.resultado());

        parada.setMotivoFallo(
                request.resultado() == EstadoParada.FALLIDA
                        ? request.motivoFallo()
                        : null
        );

        parada.setFechaRegistro(LocalDateTime.now());

        Parada paradaGuardada = paradaRepository.save(parada);

        // Determinar el nuevo estado de la solicitud
        EstadoSolicitud nuevoEstado;

        if (request.resultado() == EstadoParada.REALIZADA) {
        nuevoEstado = EstadoSolicitud.RETIRADA;
        } else {
        nuevoEstado = EstadoSolicitud.FALLIDA;
        }

        // Preparar la información que enviaremos a solicitudes-service
        ActualizarEstadoSolicitudRequest actualizacion =
                new ActualizarEstadoSolicitudRequest(
                        nuevoEstado,
                        parada.getHojaRuta().getCamionId(),
                        parada.getId(),
                        parada.getMotivoFallo()
                );

        // Notificar al microservicio de solicitudes
        try {
        solicitudesClient.actualizarEstado(
                parada.getSolicitudId(),
                actualizacion
        );

        } catch (Exception ex) {
        logger.error(
                "No se pudo actualizar la solicitud {} en solicitudes-service",
                parada.getSolicitudId(),
                ex
        );
        }

        return paradaMapper.toResponse(paradaGuardada);
    }
}
