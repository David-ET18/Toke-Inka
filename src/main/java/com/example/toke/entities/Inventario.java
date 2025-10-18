package com.example.toke.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_producto", "id_talla"})
})
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_talla", nullable = false)
    private Talla talla;
    
    @Column(nullable = false)
    private int stock;
}