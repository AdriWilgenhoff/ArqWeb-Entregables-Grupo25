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

    /**
     * Verifica si la tarifa está vigente en una fecha específica.
     * @param fecha la fecha a verificar
     * @return true si la tarifa está vigente en esa fecha
     */
    public boolean estaVigenteEn(LocalDate fecha) {
        boolean despuesDeFechaDesde = !fecha.isBefore(fechaVigenciaDesde);
        boolean antesDeFechaHasta = fechaVigenciaHasta == null || !fecha.isAfter(fechaVigenciaHasta);
        return despuesDeFechaDesde && antesDeFechaHasta;
    }


    /**
     * Verifica si esta tarifa es "abierta" (sin fecha de fin).
     * @return true si no tiene fecha de fin de vigencia
     */
    public boolean esAbierta() {
        return fechaVigenciaHasta == null;
    }
}

