package edu.tudai.arq.paradaservice.repository;

import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParadaRepository extends MongoRepository<Parada, String> {

}


