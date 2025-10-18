package com.example.toke.dto;
import com.example.toke.entities.enums.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDetalleDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private BigDecimal total;
    private String nombreCliente;

    // Dirección de envío
    private String direccionEnvio;
    private String ciudadEnvio;
    private String paisEnvio;

    // Lista de productos en el pedido
    private List<DetallePedidoDTO> detalles;
}