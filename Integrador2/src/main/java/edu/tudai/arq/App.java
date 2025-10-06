package edu.tudai.arq;

import edu.tudai.arq.dto.CarreraDTOCant;
import edu.tudai.arq.dto.EstudianteDTO;
import edu.tudai.arq.dto.ReporteCarreraDTO;
import edu.tudai.arq.entity.Carrera;
import edu.tudai.arq.entity.Estudiante;
import edu.tudai.arq.entity.Genero;
import edu.tudai.arq.entity.Inscripcion;
import edu.tudai.arq.factory.FactoryEntityManager;
import edu.tudai.arq.repository.CarreraRepositoryImpl;
import edu.tudai.arq.repository.EstudianteRepositoryImpl;
import edu.tudai.arq.repository.InscripcionRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

public class App
{
    public static void main( String[] args ) throws SQLException {

        FactoryEntityManager emf = FactoryEntityManager.getDAOFactory(FactoryEntityManager.MYSQL);
        EntityManager em = emf.createEntityManager();

        EstudianteRepositoryImpl estudianteRepository = emf.getEstudianteRepository(em);
        CarreraRepositoryImpl carreraRepository = emf.getCarreraRepository(em);
        InscripcionRepositoryImpl inscripcionRepository = emf.getInscripcionRepository(em);


        //a) Dar de alta un estudiante
        System.out.println("\n***************** CONSIGNA 2-A *****************");
        estudianteRepository.create(new Estudiante("Adrian","Wilgenhoff", LocalDate.of(1988,07,01), Genero.MALE,33356953L,"Tandil",123456L));
        EstudianteDTO e = estudianteRepository.findByNroLibreta(123456L);
        if (e != null) {
            System.out.println(e);
        }

        //b) Matricular un estudiante en una carrera
        System.out.println("\n***************** CONSIGNA 2-B *****************");
        Carrera carrera = carreraRepository.getCarreraByName("TUDAI");
        Estudiante estudiante = estudianteRepository.findById(105L);
        Inscripcion inscripcion = new Inscripcion(estudiante, carrera);
        inscripcionRepository.create(inscripcion);

        //c) Recuperar todos los estudiantes, y especificar algún criterio de ordenamiento simple.
        System.out.println("\n***************** CONSIGNA 2-C *****************");
        List<EstudianteDTO> estudiantesOrdenApellido = estudianteRepository.getEstudiantesOrderByApellido();
        estudiantesOrdenApellido.forEach(System.out::println);


        //d) Recuperar un estudiante, en base a su número de libreta universitaria.
        System.out.println("\n***************** CONSIGNA 2-D *****************");
        e = estudianteRepository.findByNroLibreta(104L);
        if (e != null) {
            System.out.println(e);
        }

        //e) Recuperar todos los estudiantes, en base a su género.
        System.out.println("\n***************** CONSIGNA 2-E *****************");
        List<EstudianteDTO> estudiantesSegunGenero = estudianteRepository.findAllByGenero(Genero.NON_BINARY);
        estudiantesSegunGenero.forEach(System.out::println);


        //f) Recuperar las carreras con estudiantes inscriptos, y ordenar por cantidad de inscriptos.
        System.out.println("\n***************** CONSIGNA 2-F *****************");
        List<CarreraDTOCant> carrerasCantInscriptos = carreraRepository.getCarrerasConInscriptosOrdenadas();
        carrerasCantInscriptos.forEach(System.out::println);

        //g) Recuperar los estudiantes de una determinada carrera, filtrado por ciudad de residencia.
        System.out.println("\n***************** CONSIGNA 2-G *****************");
        List<EstudianteDTO> estudiantesByCarreraAndLocalidad = estudianteRepository.getEstudiantesByCarreraAndCiudadResidencia(1L, "Tandil");
        estudiantesByCarreraAndLocalidad.forEach(System.out::println);

        /**
         * Generar un reporte de las carreras, que para cada carrera incluya información de los inscriptos y
         * egresados por año. Se deben ordenar las carreras alfabéticamente, y presentar los años de manera cronológica.
         */
        System.out.println("\n***************** CONSIGNA 3 *****************");
        List<ReporteCarreraDTO> reporteCarreras = carreraRepository.generarReporteCarreras();
        reporteCarreras.forEach(System.out::println);


        System.out.println("\n***************** TEST *****************");
        estudiante = estudianteRepository.findById(20L);
        estudiante.getInscripciones().forEach(System.out::println);

        em.close();
        emf.closeEntityManagerFactory();

    }

}
