package edu.tudai.arq.integrador3.repository;

import edu.tudai.arq.integrador3.model.Estudiante;
import edu.tudai.arq.integrador3.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findAllByGenero(Genero genero);

    Optional<Estudiante> findByLu(Long lu);

    boolean existsByLu(Long lu);
    boolean existsByDni(Long dni);

    @Query("SELECT e " + "FROM Estudiante e " + "JOIN Inscripcion ec ON ec.estudiante = e " + "WHERE ec.carrera.idCarrera = :carreraId AND e.ciudadResidencia = :ciudadResidencia")
    List<Estudiante> getEstudiantesByCarreraAndCiudad(
            @Param("carreraId") Long idCarrera,
            @Param("ciudadResidencia") String ciudadResidencia
    );

}
