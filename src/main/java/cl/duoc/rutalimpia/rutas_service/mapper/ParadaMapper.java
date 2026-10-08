package cl.duoc.rutalimpia.rutas_service.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.rutalimpia.rutas_service.dto.ParadaResponse;
import cl.duoc.rutalimpia.rutas_service.model.Parada;

@Component
public class ParadaMapper {

    public ParadaResponse toResponse(Parada parada) {

        return new ParadaResponse(
            parada.getId(),
            parada.getSolicitudId(),
            parada.getFolio(),
            parada.getDireccion(),
            parada.getComuna(),
            parada.getTipoResiduo(),
            parada.getBloqueHorario(),
            parada.getOrden(),
            parada.getEstado(),
            parada.getMotivoFallo()
        );
    }
}