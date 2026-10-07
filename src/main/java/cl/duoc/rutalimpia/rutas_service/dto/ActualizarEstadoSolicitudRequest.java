package cl.duoc.rutalimpia.rutas_service.dto;

import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoSolicitud;

public record ActualizarEstadoSolicitudRequest(

    EstadoSolicitud estado,

    Long camionId,

    Long paradaId,

    String motivoFallo

) {}