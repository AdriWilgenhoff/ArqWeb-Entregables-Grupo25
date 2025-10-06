package edu.tudai.arq;



import edu.tudai.arq.dto.CarreraDTOCant;
import edu.tudai.arq.dto.EstudianteDTO;
import edu.tudai.arq.dto.ReporteCarreraDTO;
import edu.tudai.arq.entity.Estudiante;
import edu.tudai.arq.entity.Genero;
import edu.tudai.arq.factory.FactoryEntityManager;
import edu.tudai.arq.repository.CarreraRepositoryImpl;
import edu.tudai.arq.repository.EstudianteRepositoryImpl;
import edu.tudai.arq.repository.InscripcionRepositoryImpl;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoriosIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EstudianteRepositoryImpl estudianteRepository;
    private CarreraRepositoryImpl carreraRepository;
    private InscripcionRepositoryImpl inscripcionRepository;

    // --- SETUP ---
    @BeforeAll
    static void setUpAll() throws SQLException {
        FactoryEntityManager factory = FactoryEntityManager.getDAOFactory(FactoryEntityManager.MYSQL);
        if (factory != null) {
            emf = factory.getEntityManagerFactory();
        } else {
            throw new IllegalStateException("No se pudo inicializar el Factory de EntityManager");
        }
    }

    // --- SETUP POR PRUEBA ---
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin(); // Inicia la transacción
        estudianteRepository = new EstudianteRepositoryImpl(em);
        carreraRepository = new CarreraRepositoryImpl(em);
        inscripcionRepository = new InscripcionRepositoryImpl(em);

    }


    // --- Clean ---
    @AfterEach
    void tearDown() {
        // Revierte la transacción, lo que asegura que cualquier cambio hecho por un test
        // (como el Estudiante creado en el Test 1) no afecte a la BD real.
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    // --- final Clean  ---
    @AfterAll
    static void tearDownAll() {
        try {
            FactoryEntityManager factory = FactoryEntityManager.getDAOFactory(FactoryEntityManager.MYSQL);
            if (factory != null) {
                factory.closeEntityManagerFactory();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cerrar el EntityManagerFactory: " + e.getMessage());
        }
    }

    // --- 2. Implement ---

    // ** 2-A/2-D:
    @Test
    @Order(1)
    @DisplayName("2-A, 2-D: Crear estudiante y buscarlo por LU")
    void testCrearYBuscarEstudiantePorLU() {


        Estudiante nuevo = new Estudiante("Juan","Genova", LocalDate.of(1983,07,27), Genero.MALE,30378913L,"Tandil",123457L);

        estudianteRepository.create(nuevo);

        EstudianteDTO encontrado = estudianteRepository.findByNroLibreta(123457L);

        assertNotNull(encontrado, "El estudiante no debe ser nulo después de la creación y búsqueda.");
        assertEquals("Juan", encontrado.getNombre());
        assertEquals(123457L, encontrado.getLu());
    }

    //  2-C
    @Test
    @Order(2)
    @DisplayName("2-C: Recuperar estudiantes ordenados por Apellido")
    void testGetEstudiantesOrderByApellido() {

        List<EstudianteDTO> listaOrdenada = estudianteRepository.getEstudiantesOrderByApellido();


        assertNotNull(listaOrdenada);
        assertEquals(104, listaOrdenada.size(), "Debe haber 104 estudiantes en total según integrador2.sql.");

        // Verifica orden alfabético
        assertEquals("Airy", listaOrdenada.get(0).getApellido());
        assertEquals("Audrey", listaOrdenada.get(1).getApellido());
        assertEquals("Bayle", listaOrdenada.get(2).getApellido());
    }

    // 2-E
    @Test
    @Order(3)
    @DisplayName("2-E: Recuperar estudiantes por género")
    void testFindAllByGenero() {

        List<EstudianteDTO> hombres = estudianteRepository.findAllByGenero(Genero.MALE);
        List<EstudianteDTO> mujeres = estudianteRepository.findAllByGenero(Genero.FEMALE);

        assertNotNull(hombres);
        assertNotNull(mujeres);

        assertEquals(46, hombres.size(), "Debe haber 56 estudiantes con género MALE.");
        assertEquals(45, mujeres.size(), "Debe haber 45 estudiantes con género FEMALE.");
    }

    // 2-F
    @Test
    @Order(4)
    @DisplayName("2-F: Carreras por cantidad de inscriptos")
    void testGetCarrerasConInscriptosOrdenadas() {

        List<CarreraDTOCant> reporte = carreraRepository.getCarrerasConInscriptosOrdenadas();


        assertNotNull(reporte);
        assertEquals(15, reporte.size(), "Debe haber 15 carreras en total.");


        assertEquals("TUDAI", reporte.get(0).getNombre(), "TUDAI debe ser la primera carrera.");
        assertEquals(17L, reporte.get(0).getCantInscriptos(), "TUDAI debe tener 17 inscriptos.");

        assertEquals(12L, reporte.get(1).getCantInscriptos());
    }

    //  2-G
    @Test
    @Order(5)
    @DisplayName("2-G: Estudiantes por Carrera y Ciudad")
    void testGetEstudiantesByCarreraAndCiudadResidencia() {

        List<EstudianteDTO> estudiantesPaqueraTUDAI = estudianteRepository.getEstudiantesByCarreraAndCiudadResidencia(1L, "Paquera");
        List<EstudianteDTO> estudiantesRauchTUDAI = estudianteRepository.getEstudiantesByCarreraAndCiudadResidencia(1L, "Rauch");


        assertEquals(1, estudiantesPaqueraTUDAI.size());
        assertEquals("Derek", estudiantesPaqueraTUDAI.get(0).getNombre());


        assertEquals(4, estudiantesRauchTUDAI.size());
        // Se verifica que el apellido de uno de los 4 estudiantes de Rauch esté
        assertTrue(estudiantesRauchTUDAI.stream().anyMatch(e -> e.getApellido().equals("Yañez")));
    }

    //  3
    @Test
    @Order(6)
    @DisplayName("3: Generar Reporte de Carreras")
    void testGenerarReporteCarreras() {

        List<ReporteCarreraDTO> reporte = carreraRepository.generarReporteCarreras();


        assertNotNull(reporte);

        assertTrue(reporte.size() > 40, "El reporte debe contener más de 40 filas (años-carrera).");


        List<ReporteCarreraDTO> tuai2022 = reporte.stream()
                .filter(r -> "TUDAI".equals(r.getNombreCarrera()) && r.getAnio() == 2022)
                .collect(Collectors.toList());

        assertEquals(1, tuai2022.size(), "Debe encontrarse la fila TUDAI 2022 en el reporte.");
        assertEquals(2L, tuai2022.get(0).getCantInscriptos(), "TUDAI 2022: 2 inscriptos esperados.");
        assertEquals(7L, tuai2022.get(0).getCantGraduados(), "TUDAI 2022: 7 graduados esperados.");


        List<ReporteCarreraDTO> sistemas2020 = reporte.stream()
                .filter(r -> "Ingenieria de Sistemas".equals(r.getNombreCarrera()) && r.getAnio() == 2020)
                .collect(Collectors.toList());

        assertEquals(1, sistemas2020.size(), "Debe encontrarse la fila Ing. Sistemas 2020 en el reporte.");
        assertEquals(2L, sistemas2020.get(0).getCantInscriptos(), "Ing. Sistemas 2020: 2 inscriptos esperados.");
        assertEquals(0L, sistemas2020.get(0).getCantGraduados(), "Ing. Sistemas 2020: 0 graduados esperados.");
    }
}