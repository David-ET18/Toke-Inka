package com.example.toke.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reseñas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_usuario", "id_producto"}) // Un usuario solo puede reseñar un producto una vez
})
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int calificacion; // Ej: de 1 a 5

    @Column(length = 2000)
    private String comentario;

    @Column(name = "fecha_reseña", nullable = false)
    private LocalDateTime fechaResena = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
}