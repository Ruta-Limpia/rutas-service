package cl.duoc.rutalimpia.rutas_service.dto;

import java.time.LocalDate;
import java.util.List;

import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoHojaRuta;

public record HojaRutaResponse(

    Long id,

    Long camionId,

    Long conductorId,

    LocalDate fecha,

    EstadoHojaRuta estado,

    List<ParadaResponse> paradas

) {}
