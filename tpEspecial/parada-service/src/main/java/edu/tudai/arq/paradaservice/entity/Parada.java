package edu.tudai.arq.paradaservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "parada")
public class Parada implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parada")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Integer latitud;

    @Column(nullable = false)
    private Integer longitud;

    @Column(nullable = false)
    private Integer capacidad;

    public Parada(String nombre, Integer latitud, Integer longitud, Integer capacidad) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.capacidad = capacidad;
    }
}

