package cl.duoc.rutalimpia.rutas_service.dto;

import java.time.LocalDate;

import cl.duoc.rutalimpia.rutas_service.model.enums.BloqueHorario;
import cl.duoc.rutalimpia.rutas_service.model.enums.TipoResiduo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgregarParadaRequest(

    @NotNull
    Long solicitudId,

    @NotBlank
    String folio,

    @NotBlank
    String direccion,

    @NotBlank
    String comuna,

    @NotNull
    TipoResiduo tipoResiduo,

    @NotNull
    LocalDate fecha,

    @NotNull
    BloqueHorario bloqueHorario,

    @NotNull
    Long camionId,

    @NotNull
    Long conductorId,

    @NotNull
    @Min(1)
    Integer capacidadParadas

) {}
