package cl.duoc.rutalimpia.rutas_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import cl.duoc.rutalimpia.rutas_service.dto.AgregarParadaRequest;
import cl.duoc.rutalimpia.rutas_service.dto.ParadaResponse;
import cl.duoc.rutalimpia.rutas_service.dto.RegistrarResultadoRequest;

import cl.duoc.rutalimpia.rutas_service.repository.ParadaRepository;
import cl.duoc.rutalimpia.rutas_service.service.ParadaService;

@RestController
@RequestMapping("/api/v1")
public class ParadaController {

    private final ParadaService paradaService;
    private final ParadaRepository paradaRepository;

    public ParadaController(
            ParadaService paradaService,
            ParadaRepository paradaRepository
    ) {
        this.paradaService = paradaService;
        this.paradaRepository = paradaRepository;
    }

    // Agregar una parada desde otro microservicio
    @PostMapping("/internal/rutas/paradas")
    public ResponseEntity<ParadaResponse> agregarParada(
            @Valid @RequestBody AgregarParadaRequest request
    ) {

        boolean yaExiste = paradaRepository
                .findBySolicitudId(request.solicitudId())
                .isPresent();

        ParadaResponse respuesta =
                paradaService.agregarParada(request);

        if (yaExiste) {
            return ResponseEntity.ok(respuesta);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // Registrar resultado del retiro
    @PatchMapping("/rutas/paradas/{paradaId}/resultado")
    @PreAuthorize("hasRole('CONDUCTOR')")
    public ResponseEntity<ParadaResponse> registrarResultado(
            @PathVariable Long paradaId,
            @Valid @RequestBody RegistrarResultadoRequest request,
            @AuthenticationPrincipal Long conductorId
    ) {

        ParadaResponse respuesta =
                paradaService.registrarResultado(
                        paradaId,
                        request,
                        conductorId
                );

        return ResponseEntity.ok(respuesta);
    }
}
