package cl.duoc.rutalimpia.rutas_service.service.impl;

import java.time.LocalDate;
import java.util.List;
import cl.duoc.rutalimpia.rutas_service.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.rutalimpia.rutas_service.dto.HojaRutaResponse;
import cl.duoc.rutalimpia.rutas_service.mapper.HojaRutaMapper;
import cl.duoc.rutalimpia.rutas_service.model.HojaRuta;
import cl.duoc.rutalimpia.rutas_service.repository.HojaRutaRepository;
import cl.duoc.rutalimpia.rutas_service.service.HojaRutaService;

@Service
@Transactional(readOnly = true)
public class HojaRutaServiceImpl implements HojaRutaService {

    private final HojaRutaRepository hojaRutaRepository;
    private final HojaRutaMapper hojaRutaMapper;

    public HojaRutaServiceImpl(
            HojaRutaRepository hojaRutaRepository,
            HojaRutaMapper hojaRutaMapper
    ) {
        this.hojaRutaRepository = hojaRutaRepository;
        this.hojaRutaMapper = hojaRutaMapper;
    }

    @Override
    public HojaRutaResponse obtenerPorConductorYFecha(
            Long conductorId,
            LocalDate fecha
    ) {

        LocalDate fechaConsulta =
                fecha != null ? fecha : LocalDate.now();

        HojaRuta hojaRuta = hojaRutaRepository
                .findByConductorIdAndFecha(conductorId, fechaConsulta)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró una hoja de ruta para el conductor"
                        )
                );

        return hojaRutaMapper.toResponse(hojaRuta);
    }

    @Override
    public List<HojaRutaResponse> listar(
            LocalDate fecha,
            Long camionId
    ) {

        List<HojaRuta> hojasRuta;

        if (fecha != null && camionId != null) {

            hojasRuta = hojaRutaRepository
                    .findByFechaAndCamionId(fecha, camionId);

        } else if (fecha != null) {

            hojasRuta = hojaRutaRepository.findByFecha(fecha);

        } else {

            hojasRuta = hojaRutaRepository.findAll();

            if (camionId != null) {
                hojasRuta = hojasRuta.stream()
                        .filter(hoja -> camionId.equals(hoja.getCamionId()))
                        .toList();
            }
        }

        return hojasRuta.stream()
                .map(hojaRutaMapper::toResponse)
                .toList();
    }
}