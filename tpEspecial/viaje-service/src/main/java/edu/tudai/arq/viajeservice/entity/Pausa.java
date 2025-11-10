package edu.tudai.arq.viajeservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "pausa")
public class Pausa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pausa")
    private Long id;

    @Column(name = "id_viaje", nullable = false)
    private Long idViaje;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Setter
    @Column(name = "hora_fin")
    private LocalDateTime horaFin;

    @Setter
    @Column(nullable = false)
    private Boolean extendida;

    public Pausa() {
        this.horaInicio = LocalDateTime.now();
        this.extendida = false;
    }

    public Pausa(Long idViaje) {
        this.idViaje = idViaje;
        this.horaInicio = LocalDateTime.now();
        this.extendida = false;
    }

    public void finalizarPausa() {
        this.horaFin = LocalDateTime.now();
    }
}

