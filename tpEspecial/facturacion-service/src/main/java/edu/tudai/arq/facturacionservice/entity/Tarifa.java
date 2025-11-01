package edu.tudai.arq.facturacionservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@Table(name = "tarifa")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarifa", nullable = false, length = 20)
    private TipoTarifa tipoTarifa;

    @Setter
    @Column(name = "precio_por_minuto", nullable = false)
    private Double precioPorMinuto;

    @Setter
    @Column(name = "fecha_vigencia_desde", nullable = false)
    private LocalDate fechaVigenciaDesde;

    @Setter
    @Column(name = "fecha_vigencia_hasta")
    private LocalDate fechaVigenciaHasta;

    @Setter
    @Column(nullable = false)
    private Boolean activa;

    public Tarifa() {
        this.fechaVigenciaDesde = LocalDate.now();
        this.activa = true;
    }

    public Tarifa(TipoTarifa tipoTarifa, Double precioPorMinuto, LocalDate fechaVigenciaDesde) {
        this.tipoTarifa = tipoTarifa;
        this.precioPorMinuto = precioPorMinuto;
        this.fechaVigenciaDesde = fechaVigenciaDesde;
        this.activa = true;
    }

    public void desactivar(LocalDate fechaFin) {
        this.activa = false;
        this.fechaVigenciaHasta = fechaFin;
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        if (!activa) {
            return false;
        }

        boolean despuesDeFechaDesde = !fecha.isBefore(fechaVigenciaDesde);
        boolean antesDeFechaHasta = fechaVigenciaHasta == null || !fecha.isAfter(fechaVigenciaHasta);

        return despuesDeFechaDesde && antesDeFechaHasta;
    }
}

