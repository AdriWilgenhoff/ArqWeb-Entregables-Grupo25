package edu.tudai.arq.facturacionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "facturacion")
public class Facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facturacion")
    private Long id;

    @Column(name = "id_viaje", nullable = false)
    private Long idViaje;

    @Column(name = "id_cuenta", nullable = false)
    private Long idCuenta;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Setter
    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @Column(name = "tiempo_total", nullable = false)
    private Long tiempoTotal;

    @Column(name = "tiempo_pausado", nullable = false)
    private Long tiempoPausado;

    @Column(name = "id_tarifa_normal", nullable = false)
    private Long idTarifaNormal;

    @Setter
    @Column(name = "id_tarifa_extendida")
    private Long idTarifaExtendida;

    public Facturacion() {
        this.fecha = LocalDateTime.now();
        this.tiempoPausado = 0L;
    }

    public Facturacion(Long idViaje, Long idCuenta, Long tiempoTotal, Long tiempoPausado,
                       Long idTarifaNormal, Long idTarifaExtendida, Double montoTotal) {
        this.idViaje = idViaje;
        this.idCuenta = idCuenta;
        this.fecha = LocalDateTime.now();
        this.tiempoTotal = tiempoTotal;
        this.tiempoPausado = tiempoPausado;
        this.idTarifaNormal = idTarifaNormal;
        this.idTarifaExtendida = idTarifaExtendida;
        this.montoTotal = montoTotal;
    }

    public Long getTiempoSinPausas() {
        return tiempoTotal - tiempoPausado;
    }
}


