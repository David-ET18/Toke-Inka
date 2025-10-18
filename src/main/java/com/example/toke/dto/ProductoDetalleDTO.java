package com.example.toke.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
@Data
public class ProductoDetalleDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String urlImagen;
    private String nombreCategoria;
    private LocalDateTime fechaCreacion;
    // Un mapa para mostrar las tallas disponibles y su stock. Ej: {"S": 10, "M": 5}
    private Map<String, Integer> tallasDisponibles;
    private List<ResenaDTO> reseñas;
}