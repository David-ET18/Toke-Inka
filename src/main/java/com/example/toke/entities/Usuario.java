package com.example.toke.entities;

import com.example.toke.entities.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // En producción, SIEMPRE debe estar encriptada

    private String direccion;
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    // Relaciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Resena> reseñas;
}