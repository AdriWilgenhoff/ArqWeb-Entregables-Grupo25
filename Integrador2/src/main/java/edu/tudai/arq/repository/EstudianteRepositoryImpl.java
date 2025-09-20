package edu.tudai.arq.repository;


import edu.tudai.arq.dto.EstudianteDTO;
import edu.tudai.arq.entity.Estudiante;
import edu.tudai.arq.entity.Genero;
import edu.tudai.arq.repository.interfaces.EstudianteRepository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class EstudianteRepositoryImpl extends CrudRepositoryImpl<Estudiante,Long> implements EstudianteRepository{

    private static EstudianteRepositoryImpl instance;

    private EstudianteRepositoryImpl(EntityManager em){
        super(Estudiante.class, Long.class, em);
    }

    public static EstudianteRepositoryImpl getInstance(EntityManager em){
        if(instance == null){
            instance = new EstudianteRepositoryImpl(em);
        }
        return instance;
    }


    public EstudianteDTO findByNroLibreta(Long nroLibreta) {
        String jpqlQuery = "SELECT e FROM Estudiante e WHERE e.lu = :lu";
        try {
            Estudiante e = em.createQuery(jpqlQuery, Estudiante.class)
                    .setParameter("lu", nroLibreta)
                    .getSingleResult();
            return new EstudianteDTO(e);
        } catch (NoResultException ex) {
            return null;
        }
    }


     public List<EstudianteDTO> findAllByGenero(Genero genero){
        String jpqlQuery ="SELECT new edu.tudai.arq.dto.EstudianteDTO(e.nombre, e.apellido, e.genero, e.fechaNacimiento, e.ciudadResidencia, e.lu)" +
                "FROM Estudiante e WHERE e.genero = :genero";
        TypedQuery<EstudianteDTO> q= em.createQuery(jpqlQuery, EstudianteDTO.class);
        q.setParameter("genero",genero);

        List<EstudianteDTO> results;
        try {
            results = q.getResultList();
        } catch (NoResultException e) {
            results = null;
        }

        return results;

    }


    @Override
    public List<EstudianteDTO> getEstudiantesOrderByApellido() {
        final String jpql =
                "SELECT new edu.tudai.arq.dto.EstudianteDTO(" +
                        " e.nombre, e.apellido, e.genero, e.fechaNacimiento, e.ciudadResidencia, e.lu) " +
                        "FROM Estudiante e ORDER BY e.apellido ASC";

        return em.createQuery(jpql, EstudianteDTO.class).getResultList();
    }

    @Override
    public List<EstudianteDTO> getEstudiantesByCarreraAndCiudadResidencia(Long idCarrera, String ciudadResidencia) {
        String jpql =
                "SELECT new edu.tudai.arq.dto.EstudianteDTO(e.nombre, e.apellido, e.genero, e.fechaNacimiento, e.ciudadResidencia, e.lu) " +
                        "FROM Estudiante e " +
                        "JOIN Inscripcion ec ON ec.estudiante = e " +
                        "WHERE ec.carrera.id = :carreraId AND e.ciudadResidencia = :ciudadResidencia";

        return em.createQuery(jpql, edu.tudai.arq.dto.EstudianteDTO.class)
                .setParameter("carreraId", idCarrera)
                .setParameter("ciudadResidencia", ciudadResidencia)
                .getResultList();
    }



}

