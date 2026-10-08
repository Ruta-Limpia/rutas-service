package cl.duoc.rutalimpia.rutas_service.service.impl;

import java.time.LocalDateTime;

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

    private final ParadaRepository paradaRepository;
    private final HojaRutaRepository hojaRutaRepository;
    private final ParadaMapper paradaMapper;

    public ParadaServiceImpl(
            ParadaRepository paradaRepository,
            HojaRutaRepository hojaRutaRepository,
            ParadaMapper paradaMapper
    ) {
        this.paradaRepository = paradaRepository;
        this.hojaRutaRepository = hojaRutaRepository;
        this.paradaMapper = paradaMapper;
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
            throw new IllegalStateException(
                    "La hoja de ruta ya está finalizada"
            );
        }

        // Verificar la capacidad del camión
        long cantidadParadas = paradaRepository
                .countByHojaRutaId(hojaRuta.getId());

        if (cantidadParadas >= request.capacidadParadas()) {
            throw new IllegalStateException(
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
            throw new IllegalStateException(
                    "La parada ya tiene un resultado registrado"
            );
        }

        // Validar el resultado
        if (request.resultado() != EstadoParada.REALIZADA
                && request.resultado() != EstadoParada.FALLIDA) {

            throw new IllegalStateException(
                    "El resultado debe ser REALIZADA o FALLIDA"
            );
        }

        // Exigir motivo cuando el retiro falle
        if (request.resultado() == EstadoParada.FALLIDA
                && (request.motivoFallo() == null
                || request.motivoFallo().isBlank())) {

            throw new IllegalStateException(
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

        // Guardar los cambios
        Parada paradaGuardada = paradaRepository.save(parada);

        // Devolver la respuesta
        return paradaMapper.toResponse(paradaGuardada);
    }
}
