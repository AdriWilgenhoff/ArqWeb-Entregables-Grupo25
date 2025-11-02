package edu.tudai.arq.paradaservice.repository;

import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParadaRepository extends JpaRepository<Parada, Long> {

    List<Parada> findByMonopatinesDisponiblesGreaterThan(Integer cantidad);

    @Query("SELECT p FROM Parada p WHERE p.monopatinesDisponibles < p.capacidad")
    List<Parada> findParadasConEspacio();

    @Query("SELECT p FROM Parada p WHERE " +
            "(6371 * acos(cos(radians(:latitud)) * cos(radians(p.latitud)) * " +
            "cos(radians(p.longitud) - radians(:longitud)) + " +
            "sin(radians(:latitud)) * sin(radians(p.latitud)))) <= :radioKm")
    List<Parada> findParadasCercanas(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm
    );
}

