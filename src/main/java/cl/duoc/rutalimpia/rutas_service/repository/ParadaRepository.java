package cl.duoc.rutalimpia.rutas_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.rutalimpia.rutas_service.model.Parada;

public interface ParadaRepository extends JpaRepository<Parada, Long> {

    Optional<Parada> findBySolicitudId(Long solicitudId);

    long countByHojaRutaId(Long hojaRutaId);
}
