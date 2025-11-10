package edu.tudai.arq.monopatinservice.repository;

import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonopatinRepository extends JpaRepository<Monopatin, Long> {

    long countByEstado(EstadoMonopatin estado);

    List<Monopatin> findByIdIn(List<Long> ids);

}