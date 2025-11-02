package edu.tudai.arq.mantenimientoservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "mantenimiento")
public class Mantenimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long id;

    @Column(name = "id_monopatin", nullable = false)
    private Long idMonopatin;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "kilometros_al_iniciar")
    private Double kilometrosAlIniciar;

    @Column(name = "tiempo_uso_al_iniciar")
    private Long tiempoUsoAlIniciar;

    @Column(name = "id_encargado")
    private String idEncargado;

    public Mantenimiento(Long idMonopatin, LocalDateTime fechaHoraInicio, String descripcion,
                        Double kilometrosAlIniciar, Long tiempoUsoAlIniciar) {
        this.idMonopatin = idMonopatin;
        this.fechaHoraInicio = fechaHoraInicio;
        this.descripcion = descripcion;
        this.kilometrosAlIniciar = kilometrosAlIniciar;
        this.tiempoUsoAlIniciar = tiempoUsoAlIniciar;
    }
}

