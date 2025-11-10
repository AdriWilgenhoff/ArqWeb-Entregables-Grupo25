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

    Optional<Tarifa> findByTipoTarifaAndFechaVigenciaHastaIsNull(TipoTarifa tipoTarifa);

    @Query("SELECT t FROM Tarifa t WHERE t.tipoTarifa = :tipo " +
            "AND t.fechaVigenciaDesde <= :fecha " +
            "AND (t.fechaVigenciaHasta IS NULL OR t.fechaVigenciaHasta >= :fecha)")
    Optional<Tarifa> findTarifaVigente(@Param("tipo") TipoTarifa tipo, @Param("fecha") LocalDate fecha);


    @Query("SELECT t FROM Tarifa t WHERE t.fechaVigenciaDesde <= CURRENT_DATE " +
            "AND (t.fechaVigenciaHasta IS NULL OR t.fechaVigenciaHasta >= CURRENT_DATE)")
    List<Tarifa> findTarifasVigentes();
}
