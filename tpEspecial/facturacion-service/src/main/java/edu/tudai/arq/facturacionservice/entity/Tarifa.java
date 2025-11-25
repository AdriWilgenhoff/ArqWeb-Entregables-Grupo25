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

    public Tarifa() {
        this.fechaVigenciaDesde = LocalDate.now();
    }

    public Tarifa(TipoTarifa tipoTarifa, Double precioPorMinuto, LocalDate fechaVigenciaDesde) {
        this.tipoTarifa = tipoTarifa;
        this.precioPorMinuto = precioPorMinuto;
        this.fechaVigenciaDesde = fechaVigenciaDesde;
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        boolean despuesDeFechaDesde = !fecha.isBefore(fechaVigenciaDesde);
        boolean antesDeFechaHasta = fechaVigenciaHasta == null || !fecha.isAfter(fechaVigenciaHasta);
        return despuesDeFechaDesde && antesDeFechaHasta;
    }

    public boolean esAbierta() {
        return fechaVigenciaHasta == null;
    }
}

