package cl.duoc.rutalimpia.rutas_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.rutalimpia.rutas_service.model.HojaRuta;

public interface HojaRutaRepository extends JpaRepository<HojaRuta, Long> {
    
    Optional<HojaRuta> findByCamionIdAndFecha(Long camionId, LocalDate fecha);

    Optional<HojaRuta> findByConductorIdAndFecha(Long conductorId, LocalDate fecha);

    List<HojaRuta> findByFecha(LocalDate fecha);

    List<HojaRuta> findByFechaAndCamionId(LocalDate fecha, Long camionId);
}
