package com.example.toke.services;

import com.example.toke.dto.EstadisticasDashboardDTO;
import com.example.toke.dto.ProductoResumenDTO;
import com.example.toke.entities.Pedido;
import com.example.toke.entities.Producto;
import com.example.toke.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @Autowired
    public DashboardService(UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
                            PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public EstadisticasDashboardDTO obtenerEstadisticas() {
        long totalUsuarios = usuarioRepository.count();
        long totalProductos = productoRepository.count();
        List<Pedido> pedidos = pedidoRepository.findAll();
        
        long totalPedidos = pedidos.size();
        BigDecimal ingresosTotales = pedidos.stream()
                                            .map(Pedido::getTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<ProductoResumenDTO> productosMasVendidos = obtenerTop5ProductosMasVendidos();

        return EstadisticasDashboardDTO.builder()
                .totalUsuarios(totalUsuarios)
                .totalProductos(totalProductos)
                .totalPedidos(totalPedidos)
                .ingresosTotales(ingresosTotales)
                .productosMasVendidos(productosMasVendidos)
                .ingresosPorCategoria(Collections.emptyMap()) // Implementación más compleja, se puede añadir después
                .build();
    }

    private List<ProductoResumenDTO> obtenerTop5ProductosMasVendidos() {
        // Obtenemos los 5 productos más vendidos usando la consulta personalizada
        List<Object[]> resultados = detallePedidoRepository.findProductosMasVendidos(PageRequest.of(0, 5));

        return resultados.stream()
                .map(this::mapToProductoResumenDTO)
                .collect(Collectors.toList());
    }

    private ProductoResumenDTO mapToProductoResumenDTO(Object[] resultado) {
        // 1. Extraer la entidad Producto (posición 0)
        Producto producto = (Producto) resultado[0];
        
        // 2. Extraer la Cantidad Vendida (posición 1)
        Long cantidadVendida = (Long) resultado[1]; 
        
        ProductoResumenDTO dto = new ProductoResumenDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setUrlImagen(producto.getUrlImagen());
        
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }
        
        // 3. ¡ASIGNAR EL DATO AL DTO!
        dto.setCantidadVendida(cantidadVendida != null ? cantidadVendida : 0L);
        
        return dto;
    }
}