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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    @Setter
    @Column(nullable = false, length = 100)
    private String apellido;

    @Setter
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Setter
    @Column(name = "numero_celular", nullable = false, length = 20)
    private String numeroCelular;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Setter
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UsuarioCuenta> cuentasAsociadas;

    public Usuario() {
        this.fechaAlta = LocalDate.now();
        this.cuentasAsociadas = new ArrayList<>();
    }

    public Usuario(String nombre, String apellido, String email, String numeroCelular,
                   String passwordHash, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.numeroCelular = numeroCelular;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.fechaAlta = LocalDate.now();
        this.cuentasAsociadas = new ArrayList<>();
    }

    public List<UsuarioCuenta> getCuentasAsociadas() {
        return new ArrayList<>(cuentasAsociadas);
    }

    public void addCuentaAsociada(UsuarioCuenta usuarioCuenta) {
        this.cuentasAsociadas.add(usuarioCuenta);
    }
}