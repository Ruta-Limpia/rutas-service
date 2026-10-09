package cl.duoc.rutalimpia.rutas_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.duoc.rutalimpia.rutas_service.dto.ActualizarEstadoSolicitudRequest;

@Component
public class SolicitudesClient {

    private final RestClient restClient;
    private final String internalKey;

    public SolicitudesClient(
            RestClient.Builder restClientBuilder,
            @Value("${app.solicitudes.url}") String solicitudesUrl,
            @Value("${app.internal.key}") String internalKey
    ) {

        this.restClient = restClientBuilder
                .baseUrl(solicitudesUrl)
                .build();

        this.internalKey = internalKey;
    }

    public void actualizarEstado(
            Long solicitudId,
            ActualizarEstadoSolicitudRequest request
    ) {

        restClient.patch()
                .uri("/api/v1/internal/solicitudes/{id}/estado", solicitudId)
                .header("X-Internal-Key", internalKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
