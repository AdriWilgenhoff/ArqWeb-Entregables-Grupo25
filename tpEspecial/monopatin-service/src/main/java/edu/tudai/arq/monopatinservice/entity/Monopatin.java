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
    private Double latitud;

    @Setter
    @Column(nullable = false)
    private Double longitud;

    @Setter
    @Column(name = "kilometros_totales", nullable = false)
    private Double kilometrosTotales;

    @Setter
    @Column(name = "tiempo_uso_total", nullable = false)
    private Long tiempoUsoTotal; // en minutos

    // CONSTRUCTORES

    public Monopatin() {
        this.kilometrosTotales = 0.0;
        this.tiempoUsoTotal = 0L;
    }

    public Monopatin(EstadoMonopatin estado, Double latitud, Double longitud) {
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometrosTotales = 0.0;
        this.tiempoUsoTotal = 0L;
    }


    public Monopatin(Long id, EstadoMonopatin estado, double latitud, double longitud, double kilometrosTotales, long tiempoUsoTotal) {
        this.id = id;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.kilometrosTotales = kilometrosTotales;
        this.tiempoUsoTotal = tiempoUsoTotal;
    }

     // Devuelve true si el monopatín se encuentra 'EN_USO'.
    public boolean estaEnUso() {
        return this.estado.equals(EstadoMonopatin.EN_USO);
    }
}