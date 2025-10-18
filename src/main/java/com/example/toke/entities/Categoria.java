package com.example.toke.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
    
    // Relaciones
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;
}