package edu.tudai.arq.integrador3.repository;

import edu.tudai.arq.integrador3.model.Inscripcion;
import edu.tudai.arq.integrador3.model.InscripcionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, InscripcionId> {

    boolean existsByEstudiante_IdEstudianteAndCarrera_IdCarrera(Long estudianteId, Long carreraId);

}