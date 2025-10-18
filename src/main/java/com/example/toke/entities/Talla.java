package com.example.toke.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tallas")
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "S", "M", "32", "40"
}