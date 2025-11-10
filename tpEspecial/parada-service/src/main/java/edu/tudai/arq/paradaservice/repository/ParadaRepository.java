package edu.tudai.arq.paradaservice.repository;

import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParadaRepository extends JpaRepository<Parada, Long> {

}

