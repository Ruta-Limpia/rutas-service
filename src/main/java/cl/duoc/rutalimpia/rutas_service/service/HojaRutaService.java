package cl.duoc.rutalimpia.rutas_service.service;

import java.time.LocalDate;
import java.util.List;

import cl.duoc.rutalimpia.rutas_service.dto.HojaRutaResponse;

public interface HojaRutaService {

    HojaRutaResponse obtenerPorConductorYFecha(
            Long conductorId,
            LocalDate fecha
    );

    List<HojaRutaResponse> listar(
            LocalDate fecha,
            Long camionId
    );

}
