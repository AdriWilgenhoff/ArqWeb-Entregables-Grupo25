package edu.tudai.arq.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "cuenta")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Long id;

    @Column(name = "numero_identificatorio", unique = true, nullable = false, length = 50)
    private String numeroIdentificatorio;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Setter
    @Column(nullable = false)
    private Double saldo;

    @Setter
    @Column(nullable = false)
    private Boolean habilitada;

    @Setter
    @Column(name = "id_cuenta_mercado_pago", nullable = false, length = 100)
    private String idCuentaMercadoPago;

    // ==================== CAMPOS PREMIUM ====================

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;

    @Setter
    @Column(name = "fecha_ultimo_pago_premium")
    private LocalDate fechaUltimoPagoPremium;

    @Setter
    @Column(name = "kilometros_disponibles", nullable = false)
    private Double kilometrosDisponibles;

    // Constantes para cuentas premium
    public static final Double MONTO_PREMIUM_MENSUAL = 500.0;
    public static final Double LIMITE_KM_GRATIS = 100.0;
    public static final Double DESCUENTO_PREMIUM = 0.5; // 50% de descuento

    @ManyToMany(mappedBy = "cuentas", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    public Cuenta() {
        this.fechaAlta = LocalDate.now();
        this.saldo = 0.0;
        this.habilitada = true;
        this.usuarios = new ArrayList<>();
        this.tipoCuenta = TipoCuenta.BASICA;
        this.kilometrosDisponibles = 0.0;
    }

    public Cuenta(String numeroIdentificatorio, String idCuentaMercadoPago) {
        this.numeroIdentificatorio = numeroIdentificatorio;
        this.idCuentaMercadoPago = idCuentaMercadoPago;
        this.fechaAlta = LocalDate.now();
        this.saldo = 0.0;
        this.habilitada = true;
        this.usuarios = new ArrayList<>();
        this.tipoCuenta = TipoCuenta.BASICA;
        this.kilometrosDisponibles = 0.0;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public void addUsuario(Usuario usuario) {
        if (!this.usuarios.contains(usuario)) {
            this.usuarios.add(usuario);
        }
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
    }

    public void cargarSaldo(Double monto) {
        if (monto > 0) {
            this.saldo += monto;
        }
    }

    public boolean descontarSaldo(Double monto) {
        if (monto > 0 && this.saldo >= monto) {
            this.saldo -= monto;
            return true;
        }
        return false;
    }

    // ==================== MÉTODOS PREMIUM ====================

    public boolean isPremium() {
        return tipoCuenta == TipoCuenta.PREMIUM;
    }

    public boolean necesitaRenovacion() {
        if (!isPremium() || fechaUltimoPagoPremium == null) {
            return false;
        }
        // Necesita renovación si pasó más de un mes desde el último pago
        LocalDate inicioMesActual = LocalDate.now().withDayOfMonth(1);
        return fechaUltimoPagoPremium.isBefore(inicioMesActual);
    }

    public void renovarCupo() {
        this.kilometrosDisponibles = LIMITE_KM_GRATIS; // Resetea a 100km
        this.fechaUltimoPagoPremium = LocalDate.now();
    }

    public boolean tieneKilometrosGratis() {
        return isPremium() && kilometrosDisponibles > 0;
    }

    /**
     * Usa kilómetros gratis disponibles.
     * @param km kilómetros a usar
     * @return kilómetros que se usaron gratis (puede ser menos si no hay suficientes)
     */
    public Double usarKilometrosGratis(Double km) {
        if (!isPremium() || km == null || km <= 0) {
            return 0.0;
        }

        double kmUsados = Math.min(km, kilometrosDisponibles);
        this.kilometrosDisponibles -= kmUsados;
        return kmUsados;
    }

    public Double getKilometrosDisponiblesParaMostrar() {
        return isPremium() ? kilometrosDisponibles : 0.0;
    }
}