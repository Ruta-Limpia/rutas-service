package cl.duoc.rutalimpia.rutas_service.dto;

import cl.duoc.rutalimpia.rutas_service.model.enums.BloqueHorario;
import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoParada;
import cl.duoc.rutalimpia.rutas_service.model.enums.TipoResiduo;

public record ParadaResponse(

    Long id,

    Long solicitudId,

    String folio,

    String direccion,

    String comuna,

    TipoResiduo tipoResiduo,

    BloqueHorario bloqueHorario,

    Integer orden,

    EstadoParada estado,

    String motivoFallo

) {}
