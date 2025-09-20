package edu.tudai.arq.repository.interfaces;

import edu.tudai.arq.dto.EstudianteDTO;
import edu.tudai.arq.entity.Estudiante;
import edu.tudai.arq.entity.Genero;

import java.util.List;

public interface EstudianteRepository {

   List<EstudianteDTO> getEstudiantesOrderByApellido();
   List<EstudianteDTO> findAllByGenero(Genero genero);
   EstudianteDTO findByNroLibreta(Long nroLibreta);
   List<EstudianteDTO> getEstudiantesByCarreraAndCiudadResidencia(Long idCarrera, String ciudadResidencia);
}
