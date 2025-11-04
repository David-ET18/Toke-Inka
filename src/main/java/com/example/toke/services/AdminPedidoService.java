package com.example.toke.services;

import com.example.toke.dto.DetallePedidoDTO;
import com.example.toke.dto.PedidoAdminDetalleDTO;
import com.example.toke.dto.PedidoAdminListaDTO;
import com.example.toke.entities.Pedido;
import com.example.toke.entities.enums.EstadoPedido;
import com.example.toke.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public AdminPedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoAdminListaDTO> listarTodosLosPedidos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc().stream()
                .map(this::mapToPedidoAdminListaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoAdminDetalleDTO obtenerDetallePedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return mapToPedidoAdminDetalleDTO(pedido);
    }

    @Transactional
    public void actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void eliminarPedido(Long id) {
        // En un sistema real, considera un "soft delete" (ej. cambiar estado a CANCELADO)
        // para no perder el historial de ventas.
        pedidoRepository.deleteById(id);
    }

    // --- MAPPERS ---

    private PedidoAdminListaDTO mapToPedidoAdminListaDTO(Pedido pedido) {
        PedidoAdminListaDTO dto = new PedidoAdminListaDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        if (pedido.getUsuario() != null) {
            dto.setNombreCliente(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
        }
        return dto;
    }

    private PedidoAdminDetalleDTO mapToPedidoAdminDetalleDTO(Pedido pedido) {
        PedidoAdminDetalleDTO dto = new PedidoAdminDetalleDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());

        if (pedido.getUsuario() != null) {
            dto.setNombreCliente(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
            dto.setEmailCliente(pedido.getUsuario().getEmail());
        }

        dto.setDetalles(pedido.getDetalles().stream().map(detalle -> {
            DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
            detalleDTO.setProductoId(detalle.getProducto().getId());
            detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
            detalleDTO.setNombreTalla(detalle.getTalla().getNombre());
            detalleDTO.setCantidad(detalle.getCantidad());
            detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleDTO.setUrlImagen(detalle.getProducto().getUrlImagen());
            return detalleDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
}