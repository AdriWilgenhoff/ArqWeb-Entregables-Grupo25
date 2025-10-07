package edu.tudai.arq.repository.interfaces;

import java.io.Serializable;
import java.util.List;

public interface CrudRepository<Entity, ID extends Serializable> {

    Entity findById(Long id);
    List<Entity> findAll();
    void create(Entity entity);
    void update(Entity entity);
    void delete(Entity entity);

}
