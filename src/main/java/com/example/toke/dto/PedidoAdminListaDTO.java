package com.example.toke.dto;

import com.example.toke.entities.enums.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoAdminListaDTO {
    private Long id;
    private String nombreCliente;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private EstadoPedido estado;
}