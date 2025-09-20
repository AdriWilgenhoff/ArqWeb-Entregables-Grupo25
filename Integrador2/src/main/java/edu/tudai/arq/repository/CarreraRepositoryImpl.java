package edu.tudai.arq.repository;

import edu.tudai.arq.dto.CarreraDTOCant;
import edu.tudai.arq.dto.ReporteCarreraDTO;
import edu.tudai.arq.entity.Carrera;
import edu.tudai.arq.repository.interfaces.CarreraRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class CarreraRepositoryImpl extends  CrudRepositoryImpl<Carrera,Long>  implements CarreraRepository{

    private static CarreraRepositoryImpl instance;

    private CarreraRepositoryImpl(EntityManager em) {
        super(Carrera.class, Long.class, em);
    }

    public static CarreraRepositoryImpl getInstance(EntityManager em){
        if(instance == null){
            instance = new CarreraRepositoryImpl(em);
        }

        return instance;
    }

    @Override
    public Carrera getCarreraByName(String nombre) {
        String jpql = "SELECT c FROM Carrera c WHERE LOWER(c.nombre) = LOWER(:nombre)";
        return em.createQuery(jpql, Carrera.class)
                .setParameter("nombre", nombre)
                .getSingleResult();
    }

    @Override
    public List<CarreraDTOCant> getCarrerasConInscriptosOrdenadas() {
        String jpql =
                "SELECT new edu.tudai.arq.dto.CarreraDTOCant(c, COUNT(ins)) " +
                        "FROM Carrera c JOIN c.inscriptos ins " +
                        "GROUP BY c.idCarrera, c.nombre " +
                        "HAVING COUNT(ins) > 0 " +
                        "ORDER BY COUNT(ins) DESC";

        return em.createQuery(jpql, edu.tudai.arq.dto.CarreraDTOCant.class)
                .getResultList();
    }


   //REVISAR
   @Override
   public List<ReporteCarreraDTO> generarReporteCarreras() {
         String sql =
                "SELECT c.nombre AS nombre_carrera, " +
                        "       y.anio   AS anio, " +
                        "       SUM(CASE WHEN y.tipo = 'G' THEN y.cnt ELSE 0 END) AS cant_graduados, " +
                        "       SUM(CASE WHEN y.tipo = 'I' THEN y.cnt ELSE 0 END) AS cant_inscriptos " +
                        "FROM Carrera c " +
                        "JOIN ( " +
                        "    SELECT i.id_carrera AS id_carrera, YEAR(i.fecha_inscripcion) AS anio, 'I' AS tipo, COUNT(*) AS cnt " +
                        "    FROM Inscripcion i " +
                        "    WHERE i.fecha_inscripcion IS NOT NULL " +
                        "    GROUP BY i.id_carrera, YEAR(i.fecha_inscripcion) " +
                        "    UNION ALL " +
                        "    SELECT i.id_carrera AS id_carrera, YEAR(i.fecha_egreso) AS anio, 'G' AS tipo, COUNT(*) AS cnt " +
                        "    FROM Inscripcion i " +
                        "    WHERE i.fecha_egreso IS NOT NULL " +
                        "    GROUP BY i.id_carrera, YEAR(i.fecha_egreso) " +
                        ") y ON y.id_carrera = c.id_carrera " +
                        "WHERE y.anio IS NOT NULL " +
                        "GROUP BY c.nombre, y.anio " +
                        "ORDER BY c.nombre ASC, y.anio ASC";

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        List<ReporteCarreraDTO> reporte = new ArrayList<>();
        for (Object[] r : rows) {
            String nombreCarrera = (String) r[0];
            int anio             = ((Number) r[1]).intValue();
            long cantGraduados   = ((Number) r[2]).longValue();
            long cantInscriptos  = ((Number) r[3]).longValue();

            reporte.add(new ReporteCarreraDTO(nombreCarrera, anio, cantGraduados, cantInscriptos));
        }
        return reporte;
    }

}

