package edu.tudai.arq.viajeservice.repository;

import edu.tudai.arq.viajeservice.entity.EstadoViaje;
import edu.tudai.arq.viajeservice.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    // Buscar viajes por cuenta
    List<Viaje> findByIdCuenta(Long idCuenta);

    // Buscar viajes por usuario
    List<Viaje> findByIdUsuario(Long idUsuario);

    // Buscar viajes por monopatín
    List<Viaje> findByIdMonopatin(Long idMonopatin);

    // Buscar viajes por estado
    List<Viaje> findByEstado(EstadoViaje estado);

    // Buscar viajes activos (EN_CURSO o PAUSADO) de un monopatín
    @Query("SELECT v FROM Viaje v WHERE v.idMonopatin = :idMonopatin AND v.estado IN ('EN_CURSO', 'PAUSADO')")
    Optional<Viaje> findViajeActivoByMonopatin(@Param("idMonopatin") Long idMonopatin);

    // Buscar viajes activos de un usuario
    @Query("SELECT v FROM Viaje v WHERE v.idUsuario = :idUsuario AND v.estado IN ('EN_CURSO', 'PAUSADO')")
    List<Viaje> findViajesActivosByUsuario(@Param("idUsuario") Long idUsuario);

    // Contar viajes por monopatín en un año específico
    @Query("SELECT COUNT(v) FROM Viaje v WHERE v.idMonopatin = :idMonopatin AND YEAR(v.fechaHoraInicio) = :anio")
    Long countViajesByMonopatinAndAnio(@Param("idMonopatin") Long idMonopatin, @Param("anio") int anio);
}


