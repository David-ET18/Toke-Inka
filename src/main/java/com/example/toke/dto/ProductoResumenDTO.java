package com.example.toke.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoResumenDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String urlImagen;
    private String nombreCategoria;
    private Long cantidadVendida; 
}