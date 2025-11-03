package edu.tudai.arq.repository;

import edu.tudai.arq.entity.MantenimientoService;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MantenimientoServiceRepository extends JpaRepository<MantenimientoService, Long> {
    @Query("SELECT m FROM MantenimientoService m WHERE m.idMonopatin = :idMonopatin AND m.fechaHoraFin IS NULL")
    Optional<MantenimientoService> findActivoByMonopatin(@Param("idMonopatin") Long idMonopatin);

    boolean existsByMonopatinAndFechaHoraFinIsNull(Monopatin monopatin);
}
