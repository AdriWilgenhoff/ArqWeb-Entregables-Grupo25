package edu.tudai.arq.factory;

import edu.tudai.arq.repository.CarreraRepositoryImpl;
import edu.tudai.arq.repository.EstudianteRepositoryImpl;
import edu.tudai.arq.repository.InscripcionRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MySQLFactory extends FactoryEntityManager {

    private static MySQLFactory instance;
    private EntityManagerFactory emf;

    private MySQLFactory() {
        this.emf = Persistence.createEntityManagerFactory("MySqlPersistenceUnit");
    }

    public static MySQLFactory getInstance() {
        if(instance == null){
            instance = new MySQLFactory();
        }
        return instance;
    }

    @Override
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public void closeEntityManagerFactory() {
        emf.close();
    }
    
    @Override
    public CarreraRepositoryImpl getCarreraRepository(EntityManager em) {
        return CarreraRepositoryImpl.getInstance(em);
    }
    @Override
    public EstudianteRepositoryImpl getEstudianteRepository(EntityManager em) {
        return EstudianteRepositoryImpl.getInstance(em);
    }
    @Override
    public InscripcionRepositoryImpl getInscripcionRepository(EntityManager em) {
        return InscripcionRepositoryImpl.getInstance(em);
    }

}