package com.example.toke.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetallePedidoDTO {
    private Long productoId;
    private String nombreProducto;
    private String nombreTalla;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String urlImagen;
}