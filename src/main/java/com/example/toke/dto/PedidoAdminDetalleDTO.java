package com.example.toke.dto;

import com.example.toke.entities.enums.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoAdminDetalleDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private BigDecimal total;
    
    // Info del cliente
    private String nombreCliente;
    private String emailCliente;
    
    // Info de envío
    private String direccionEnvio;
    
    // Productos
    private List<DetallePedidoDTO> detalles;
}