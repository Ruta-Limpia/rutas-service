package cl.duoc.rutalimpia.rutas_service.dto;

import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoParada;
import jakarta.validation.constraints.NotNull;

public record RegistrarResultadoRequest(

    @NotNull(message = "El resultado es obligatorio")
    EstadoParada resultado,

    String motivoFallo

) {}
