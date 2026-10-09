package cl.duoc.rutalimpia.rutas_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.rutalimpia.rutas_service.dto.HojaRutaResponse;
import cl.duoc.rutalimpia.rutas_service.service.HojaRutaService;

@RestController
@RequestMapping("/api/v1/rutas")
public class HojaRutaController {

    private final HojaRutaService hojaRutaService;

    public HojaRutaController(HojaRutaService hojaRutaService) {
        this.hojaRutaService = hojaRutaService;
    }

    // Consultar la hoja de ruta del conductor autenticado
    @GetMapping("/mi-hoja")
    @PreAuthorize("hasRole('CONDUCTOR')")
    public ResponseEntity<HojaRutaResponse> obtenerMiHoja(
            @AuthenticationPrincipal Long conductorId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {

        HojaRutaResponse respuesta =
                hojaRutaService.obtenerPorConductorYFecha(
                        conductorId,
                        fecha
                );

        return ResponseEntity.ok(respuesta);
    }

    // Listar hojas de ruta (solo administradores)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HojaRutaResponse>> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,

            @RequestParam(required = false)
            Long camionId
    ) {

        List<HojaRutaResponse> respuesta =
                hojaRutaService.listar(fecha, camionId);

        return ResponseEntity.ok(respuesta);
    }
}