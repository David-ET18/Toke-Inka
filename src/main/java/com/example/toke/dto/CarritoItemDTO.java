package com.example.toke.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarritoItemDTO {
    private Long productoId;
    private String nombreProducto;
    private Long tallaId;
    private String nombreTalla;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String urlImagen;
    private int stockDisponible;

    // Método para calcular el subtotal del item
    public BigDecimal getSubtotal() {
         if (precioUnitario == null || cantidad < 0) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
}