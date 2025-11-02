package edu.tudai.arq.monopatinservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MonopatinRepository extends JpaRepository<Monopatin, Long> {

}