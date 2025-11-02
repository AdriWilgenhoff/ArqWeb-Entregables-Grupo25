package edu.tudai.arq.mantenimientoservice.repository;

import edu.tudai.arq.mantenimientoservice.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByFechaHoraFinIsNull();

    List<Mantenimiento> findByFechaHoraFinIsNotNull();

    List<Mantenimiento> findByIdMonopatin(Long idMonopatin);
}

