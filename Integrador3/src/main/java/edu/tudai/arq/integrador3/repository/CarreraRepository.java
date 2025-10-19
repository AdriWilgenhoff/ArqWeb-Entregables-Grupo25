package edu.tudai.arq.integrador3.repository;

import edu.tudai.arq.integrador3.dto.CarreraDTOCant;
import edu.tudai.arq.integrador3.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    Carrera findByNombreIgnoreCase(String nombre);


    @Query("SELECT new edu.tudai.arq.integrador3.dto.CarreraDTOCant(c.idCarrera, c.nombre, COUNT(i)) " +
            "FROM Carrera c JOIN c.inscriptos i " +
            "GROUP BY c.idCarrera, c.nombre " +
            "HAVING COUNT(i) > 0 " +
            "ORDER BY COUNT(i) DESC")
    List<CarreraDTOCant> findCarrerasWithInscritosOrdered();


    @Query(value =
            "SELECT c.nombre AS nombreCarrera, " +
                    "       y.anio AS anio, " +
                    "       SUM(CASE WHEN y.tipo = 'G' THEN y.cnt ELSE 0 END) AS cantGraduados, " +
                    "       SUM(CASE WHEN y.tipo = 'I' THEN y.cnt ELSE 0 END) AS cantInscriptos " +
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
                    "ORDER BY c.nombre ASC, y.anio ASC",
            nativeQuery = true)
    List<Object[]> generarReporteCarreras();

}
