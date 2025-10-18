package com.example.toke.dto;

import com.example.toke.entities.enums.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResumenDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private BigDecimal total;
    private int numeroDeItems;
}