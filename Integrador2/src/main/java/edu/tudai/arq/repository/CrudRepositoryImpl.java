package edu.tudai.arq.repository;

import edu.tudai.arq.repository.interfaces.CrudRepository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;

public class CrudRepositoryImpl <Entity,ID extends Serializable> implements CrudRepository<Entity, ID>  {

    protected Class<ID> idClass;
    private Class<Entity> entityClass;
    protected EntityManager em;


    public CrudRepositoryImpl(Class<Entity> entityClass, Class<ID> idClass, EntityManager em) {
        this.entityClass=entityClass;
        this.idClass=idClass;
        this.em=em;
    }

    @Override
    public Entity findById(Long id) {
        return em.find(entityClass, id);
    }

    @Override
    public List<Entity> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
    }

    @Override
    public void create(Entity entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(Entity entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public void delete(Entity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }
}
