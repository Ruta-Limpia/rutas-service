package cl.duoc.rutalimpia.rutas_service.model;

import java.time.LocalDateTime;

import cl.duoc.rutalimpia.rutas_service.model.enums.BloqueHorario;
import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoParada;
import cl.duoc.rutalimpia.rutas_service.model.enums.TipoResiduo;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paradas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoja_ruta_id", nullable = false)
    private HojaRuta hojaRuta;

    @Column(name = "solicitud_id", nullable = false, unique = true)
    private Long solicitudId;

    @Column(name = "folio", nullable = false, length = 30)
    private String folio;

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "comuna", nullable = false, length = 80)
    private String comuna;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false)
    private TipoResiduo tipoResiduo;

    @Enumerated(EnumType.STRING)
    @Column(name = "bloque_horario", nullable = false)
    private BloqueHorario bloqueHorario;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoParada estado = EstadoParada.PENDIENTE;

    @Column(name = "motivo_fallo", length = 200)
    private String motivoFallo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
