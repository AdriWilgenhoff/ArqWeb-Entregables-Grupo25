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

    // Buscar facturación por viaje
    Optional<Facturacion> findByIdViaje(Long idViaje);

    // Buscar facturaciones por cuenta
    List<Facturacion> findByIdCuenta(Long idCuenta);

    // Buscar facturaciones en un rango de fechas
    List<Facturacion> findByFechaBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    // Buscar facturaciones de una cuenta en un rango de fechas
    @Query("SELECT f FROM Facturacion f WHERE f.idCuenta = :idCuenta " +
           "AND f.fecha BETWEEN :fechaDesde AND :fechaHasta")
    List<Facturacion> findByIdCuentaAndFechaBetween(
            @Param("idCuenta") Long idCuenta,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);

    // Calcular total facturado en un rango de fechas
    @Query("SELECT COALESCE(SUM(f.montoTotal), 0.0) FROM Facturacion f " +
           "WHERE f.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double calcularTotalFacturado(
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);

    // Calcular total facturado por cuenta en un rango de fechas
    @Query("SELECT COALESCE(SUM(f.montoTotal), 0.0) FROM Facturacion f " +
           "WHERE f.idCuenta = :idCuenta AND f.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Double calcularTotalFacturadoPorCuenta(
            @Param("idCuenta") Long idCuenta,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);
}
