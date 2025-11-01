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

    @OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UsuarioCuenta> usuariosAsociados;

    public Cuenta() {
        this.fechaAlta = LocalDate.now();
        this.saldo = 0.0;
        this.habilitada = true;
        this.usuariosAsociados = new ArrayList<>();
    }

    public Cuenta(String numeroIdentificatorio, String idCuentaMercadoPago) {
        this.numeroIdentificatorio = numeroIdentificatorio;
        this.idCuentaMercadoPago = idCuentaMercadoPago;
        this.fechaAlta = LocalDate.now();
        this.saldo = 0.0;
        this.habilitada = true;
        this.usuariosAsociados = new ArrayList<>();
    }

    public List<UsuarioCuenta> getUsuariosAsociados() {
        return new ArrayList<>(usuariosAsociados);
    }

    public void addUsuarioAsociado(UsuarioCuenta usuarioCuenta) {
        this.usuariosAsociados.add(usuarioCuenta);
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
}