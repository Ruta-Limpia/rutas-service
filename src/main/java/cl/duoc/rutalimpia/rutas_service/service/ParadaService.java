package cl.duoc.rutalimpia.rutas_service.service;

import cl.duoc.rutalimpia.rutas_service.dto.AgregarParadaRequest;
import cl.duoc.rutalimpia.rutas_service.dto.ParadaResponse;
import cl.duoc.rutalimpia.rutas_service.dto.RegistrarResultadoRequest;

public interface ParadaService {

    ParadaResponse agregarParada(AgregarParadaRequest request);

    ParadaResponse registrarResultado(
            Long paradaId,
            RegistrarResultadoRequest request,
            Long conductorId
    );

}