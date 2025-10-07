package edu.tudai.arq.factory;

import edu.tudai.arq.repository.CarreraRepositoryImpl;
import edu.tudai.arq.repository.EstudianteRepositoryImpl;
import edu.tudai.arq.repository.InscripcionRepositoryImpl;

import java.sql.SQLException;

import javax.persistence.EntityManager;


public abstract class FactoryEntityManager {
    
    public static final int MYSQL = 1;

    public abstract CarreraRepositoryImpl getCarreraRepository(EntityManager em);
    public abstract EstudianteRepositoryImpl getEstudianteRepository(EntityManager em);
    public abstract InscripcionRepositoryImpl getInscripcionRepository(EntityManager em);
    
    public abstract void closeEntityManagerFactory();
    public abstract EntityManager createEntityManager();

    public static FactoryEntityManager getDAOFactory(int persistence) throws SQLException {
        switch (persistence) {
            case MYSQL: 
                return MySQLFactory.getInstance();
            default: 
                return null;
        }
    }

}