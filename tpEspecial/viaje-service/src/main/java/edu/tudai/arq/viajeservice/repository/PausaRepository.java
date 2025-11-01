package edu.tudai.arq.viajeservice.repository;

import edu.tudai.arq.viajeservice.entity.Pausa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PausaRepository extends JpaRepository<Pausa, Long> {

    // Buscar pausas por viaje
    List<Pausa> findByIdViaje(Long idViaje);

    // Buscar pausa activa (sin hora de fin) de un viaje
    Optional<Pausa> findByIdViajeAndHoraFinIsNull(Long idViaje);
}

