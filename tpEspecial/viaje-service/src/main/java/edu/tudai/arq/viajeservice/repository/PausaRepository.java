package edu.tudai.arq.viajeservice.repository;

import edu.tudai.arq.viajeservice.entity.Pausa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PausaRepository extends JpaRepository<Pausa, Long> {

    List<Pausa> findByIdViaje(Long idViaje);

    Optional<Pausa> findByIdViajeAndHoraFinIsNull(Long idViaje);
}

