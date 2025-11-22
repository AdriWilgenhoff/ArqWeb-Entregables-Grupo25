package edu.tudai.arq.viajeservice.repository;

import edu.tudai.arq.viajeservice.entity.EstadoViaje;
import edu.tudai.arq.viajeservice.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    List<Viaje> findByIdCuenta(Long idCuenta);

    List<Viaje> findByIdUsuario(Long idUsuario);

    List<Viaje> findByIdMonopatin(Long idMonopatin);

    List<Viaje> findByEstado(EstadoViaje estado);

    @Query("SELECT v FROM Viaje v WHERE v.idMonopatin = :idMonopatin AND v.estado IN ('EN_CURSO', 'PAUSADO')")
    Optional<Viaje> findViajeActivoByMonopatin(@Param("idMonopatin") Long idMonopatin);

    @Query("SELECT v.idMonopatin FROM Viaje v WHERE YEAR(v.fechaHoraInicio) = :anio " +
           "GROUP BY v.idMonopatin HAVING COUNT(v) > :cantidadViajes")
    List<Long> findMonopatinesConMasDeXViajes(@Param("cantidadViajes") int cantidadViajes, @Param("anio") int anio);

    List<Viaje> findByFechaHoraInicioBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    List<Viaje> findByIdUsuarioAndFechaHoraInicioBetween(Long idUsuario, LocalDateTime fechaDesde, LocalDateTime fechaHasta);
}
