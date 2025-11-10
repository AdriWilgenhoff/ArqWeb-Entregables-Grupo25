package edu.tudai.arq.monopatinservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "monopatin")
public class Monopatin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoMonopatin estado;

    @Setter
    @Column(nullable = false)
    private Integer latitud;

    @Setter
    @Column(nullable = false)
    private Integer longitud;

    @Setter
    @Column(name = "kilometros_totales", nullable = false)
    private Double kilometrosTotales;

    @Setter
    @Column(name = "tiempo_uso_total", nullable = false)
    private Long tiempoUsoTotal;

    @Setter
    @Column(name = "tiempo_pausas", nullable = false)
    private Long tiempoPausas;

    public Monopatin() {
        this.kilometrosTotales = 0.0;
        this.tiempoUsoTotal = 0L;
        this.tiempoPausas = 0L;
    }

    public Monopatin(EstadoMonopatin estado, Integer latitud, Integer longitud) {
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometrosTotales = 0.0;
        this.tiempoUsoTotal = 0L;
        this.tiempoPausas = 0L;
    }

    public Monopatin(Long id, EstadoMonopatin estado, Integer latitud, Integer longitud, double kilometrosTotales, long tiempoUsoTotal, long tiempoPausas) {
        this.id = id;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometrosTotales = kilometrosTotales;
        this.tiempoUsoTotal = tiempoUsoTotal;
        this.tiempoPausas = tiempoPausas;
    }

    public boolean estaEnUso() {
        return this.estado.equals(EstadoMonopatin.EN_USO);
    }
}