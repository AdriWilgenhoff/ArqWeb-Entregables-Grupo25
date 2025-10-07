package edu.tudai.arq.repository;

import edu.tudai.arq.entity.Inscripcion;
import edu.tudai.arq.entity.InscripcionId;
import edu.tudai.arq.repository.interfaces.InscripcionRepository;

import javax.persistence.EntityManager;


public class InscripcionRepositoryImpl extends  CrudRepositoryImpl<Inscripcion,InscripcionId>  implements InscripcionRepository {

    private static InscripcionRepositoryImpl instance;

    private InscripcionRepositoryImpl(EntityManager em) {
        super(Inscripcion.class, InscripcionId.class, em);
    }

    public static InscripcionRepositoryImpl getInstance (EntityManager em){
        if(instance == null){
            instance=new InscripcionRepositoryImpl(em);
        }

        return instance;
    }
}
