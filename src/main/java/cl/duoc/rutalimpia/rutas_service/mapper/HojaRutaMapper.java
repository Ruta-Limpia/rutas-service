package cl.duoc.rutalimpia.rutas_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import cl.duoc.rutalimpia.rutas_service.dto.HojaRutaResponse;
import cl.duoc.rutalimpia.rutas_service.dto.ParadaResponse;
import cl.duoc.rutalimpia.rutas_service.model.HojaRuta;

@Component
public class HojaRutaMapper {

    private final ParadaMapper paradaMapper;

    public HojaRutaMapper(ParadaMapper paradaMapper) {
        this.paradaMapper = paradaMapper;
    }

    public HojaRutaResponse toResponse(HojaRuta hojaRuta) {

        List<ParadaResponse> paradas = hojaRuta.getParadas()
                .stream()
                .map(paradaMapper::toResponse)
                .toList();

        return new HojaRutaResponse(
                hojaRuta.getId(),
                hojaRuta.getCamionId(),
                hojaRuta.getConductorId(),
                hojaRuta.getFecha(),
                hojaRuta.getEstado(),
                paradas
        );
    }
}
