package edu.tudai.arq.paradaservice.repository;

import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParadaRepository extends JpaRepository<Parada, Long> {

}

/*
import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParadaRepository extends MongoRepository<Parada, String> {
    // MongoRepository ya provee todos los métodos básicos:
    // - save(entity)
    // - findById(id)
    // - findAll()
    // - deleteById(id)
    // - count()
    // etc.

    // Puedes agregar queries personalizadas si las necesitas:
    // List<Parada> findByNombreContaining(String nombre);
    // List<Parada> findByCapacidadGreaterThan(Integer capacidad);
}
*/
