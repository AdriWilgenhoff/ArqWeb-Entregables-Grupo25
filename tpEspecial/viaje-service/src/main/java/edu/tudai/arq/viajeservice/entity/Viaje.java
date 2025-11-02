package edu.tudai.arq.viajeservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long id;

    @Column(name = "id_cuenta", nullable = false)
    private Long idCuenta;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_monopatin", nullable = false)
    private Long idMonopatin;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Setter
    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(name = "id_parada_inicio", nullable = false)
    private Long idParadaInicio;

    @Setter
    @Column(name = "id_parada_fin")
    private Long idParadaFin;

    @Setter
    @Column(name = "kilometros_recorridos", nullable = false)
    private Double kilometrosRecorridos;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoViaje estado;

    @Setter
    @Column(name = "costo_total")
    private Double costoTotal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_viaje")
    private List<Pausa> pausas;

    public Viaje() {
        this.fechaHoraInicio = LocalDateTime.now();
        this.kilometrosRecorridos = 0.0;
        this.estado = EstadoViaje.EN_CURSO;
        this.pausas = new ArrayList<>();
    }

    public Viaje(Long idCuenta, Long idUsuario, Long idMonopatin, Long idParadaInicio) {
        this.idCuenta = idCuenta;
        this.idUsuario = idUsuario;
        this.idMonopatin = idMonopatin;
        this.idParadaInicio = idParadaInicio;
        this.fechaHoraInicio = LocalDateTime.now();
        this.kilometrosRecorridos = 0.0;
        this.estado = EstadoViaje.EN_CURSO;
        this.pausas = new ArrayList<>();
    }

    public List<Pausa> getPausas() {
        return new ArrayList<>(pausas);
    }

    public void agregarPausa(Pausa pausa) {
        this.pausas.add(pausa);
    }

    public void finalizarViaje(Long idParadaFin, Double kilometrosRecorridos, Double costoTotal) {
        this.fechaHoraFin = LocalDateTime.now();
        this.idParadaFin = idParadaFin;
        this.kilometrosRecorridos = kilometrosRecorridos;
        this.costoTotal = costoTotal;
        this.estado = EstadoViaje.FINALIZADO;
    }

    public void pausar() {
        this.estado = EstadoViaje.PAUSADO;
    }

    public void reanudar() {
        this.estado = EstadoViaje.EN_CURSO;
    }

    /**
     * Calcula el tiempo total del viaje en minutos.
     * Si el viaje aún no ha finalizado, calcula hasta el momento actual.
     */
    public long calcularTiempoTotal() {
        LocalDateTime fin = (this.fechaHoraFin != null) ? this.fechaHoraFin : LocalDateTime.now();
        return Duration.between(this.fechaHoraInicio, fin).toMinutes();
    }
}
