package com.example.toke.services;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.dto.PedidoDetalleDTO;
import com.example.toke.dto.PedidoResumenDTO;
import com.example.toke.entities.*;
import com.example.toke.entities.enums.EstadoPedido;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.exception.StockInsuficienteException;
import com.example.toke.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, InventarioRepository inventarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional
    public Pedido crearPedido(CarritoDTO carritoDTO, String userEmail, String direccionEnvio) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail).orElseThrow();

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setEstado(EstadoPedido.PAGADO); // Asumimos pago inmediato
        nuevoPedido.setTotal(carritoDTO.getTotal());
        nuevoPedido.setDireccionEnvio(direccionEnvio); // Simplificado
        nuevoPedido.setDetalles(new ArrayList<>());

        for (var item : carritoDTO.getItems()) {
            Inventario inventario = inventarioRepository.findByProductoIdAndTallaId(item.getProductoId(), item.getTallaId())
                    .orElseThrow(() -> new ProductoNoEncontradoException("Inventario no encontrado."));

            if (inventario.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para " + item.getNombreProducto());
            }

            // Reducir el stock
            inventario.setStock(inventario.getStock() - item.getCantidad());
            inventarioRepository.save(inventario);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(nuevoPedido);
            detalle.setProducto(inventario.getProducto());
            detalle.setTalla(inventario.getTalla());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            
            nuevoPedido.getDetalles().add(detalle);
        }

        return pedidoRepository.save(nuevoPedido);
    }
    
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        // Lógica para mapear a PedidoResumenDTO
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId).stream()
            .map(this::mapToPedidoResumenDTO)
            .collect(Collectors.toList());
    }

    // Aquí irían los demás mappers (mapToPedidoResumenDTO, mapToPedidoDetalleDTO, etc.)
    private PedidoResumenDTO mapToPedidoResumenDTO(Pedido pedido) {
        PedidoResumenDTO dto = new PedidoResumenDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setNumeroDeItems(pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum());
        return dto;
    }
}