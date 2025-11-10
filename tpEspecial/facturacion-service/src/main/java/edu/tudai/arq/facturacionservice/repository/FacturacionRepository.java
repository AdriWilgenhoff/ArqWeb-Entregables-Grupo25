package edu.tudai.arq.facturacionservice.repository;

import edu.tudai.arq.facturacionservice.entity.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {

    Optional<Facturacion> findByIdViaje(Long idViaje);

    List<Facturacion> findByIdCuenta(Long idCuenta);

      @Query("SELECT SUM(f.montoTotal) FROM Facturacion f " +
           "WHERE f.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double calcularTotalFacturado(
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);

}
