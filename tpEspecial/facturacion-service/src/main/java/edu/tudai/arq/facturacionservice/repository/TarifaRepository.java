package edu.tudai.arq.facturacionservice.repository;

import edu.tudai.arq.facturacionservice.entity.Tarifa;
import edu.tudai.arq.facturacionservice.entity.TipoTarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    // Buscar tarifas por tipo
    List<Tarifa> findByTipoTarifa(TipoTarifa tipoTarifa);

    // Buscar tarifas activas
    List<Tarifa> findByActiva(Boolean activa);

    // Buscar tarifas activas por tipo
    List<Tarifa> findByTipoTarifaAndActiva(TipoTarifa tipoTarifa, Boolean activa);

    // Buscar tarifa vigente por tipo y fecha
    @Query("SELECT t FROM Tarifa t WHERE t.tipoTarifa = :tipo AND t.activa = true " +
            "AND t.fechaVigenciaDesde <= :fecha " +
            "AND (t.fechaVigenciaHasta IS NULL OR t.fechaVigenciaHasta >= :fecha)")
    Optional<Tarifa> findTarifaVigente(@Param("tipo") TipoTarifa tipo, @Param("fecha") LocalDate fecha);

    // Buscar tarifas vigentes en una fecha
    @Query("SELECT t FROM Tarifa t WHERE t.activa = true " +
            "AND t.fechaVigenciaDesde <= :fecha " +
            "AND (t.fechaVigenciaHasta IS NULL OR t.fechaVigenciaHasta >= :fecha)")
    List<Tarifa> findTarifasVigentesEn(@Param("fecha") LocalDate fecha);
}

