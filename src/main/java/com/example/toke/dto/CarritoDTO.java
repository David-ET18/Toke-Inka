package com.example.toke.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CarritoDTO {
    private List<CarritoItemDTO> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    // Lógica para recalcular el total cada vez que se modifica el carrito
    public void recalcularTotal() {
        this.total = items.stream()
                           .map(CarritoItemDTO::getSubtotal)
                           .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}