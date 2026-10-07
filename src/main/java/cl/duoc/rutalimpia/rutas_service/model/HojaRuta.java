package cl.duoc.rutalimpia.rutas_service.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cl.duoc.rutalimpia.rutas_service.model.enums.EstadoHojaRuta;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(
    name = "hoja_ruta",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"camion_id", "fecha"})
    }
)

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor 


public class HojaRuta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "camion_id", nullable = false)
    private Long camionId;

    @Column (name = "conductor_id", nullable = false)
    private Long conductorId;

    @Column (name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHojaRuta estado = EstadoHojaRuta.ABIERTA;

    @OneToMany(mappedBy = "hojaRuta")
    @OrderBy("orden ASC")
    private List<Parada> paradas = new ArrayList<>();

}
