package cl.duoc.rutalimpia.rutas_service.dto;

import java.time.LocalDateTime;

public record ErrorResponse(

    LocalDateTime timestamp,

    int status,

    String error,

    String mensaje,

    String path

) {}