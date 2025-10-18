package com.example.toke.services;

import com.example.toke.dto.ProductoDetalleDTO;
import com.example.toke.dto.ProductoResumenDTO;
import com.example.toke.entities.Inventario;
import com.example.toke.entities.Producto;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.repositories.ProductoRepository;
import com.example.toke.repositories.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ResenaService resenaService; // Reutilizamos el servicio de reseñas

    @Autowired
    public ProductoService(ProductoRepository productoRepository, ResenaService resenaService) {
        this.productoRepository = productoRepository;
        this.resenaService = resenaService;
    }

    @Transactional(readOnly = true)
    public List<ProductoResumenDTO> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(this::mapToProductoResumenDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDetalleDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + id));
        return mapToProductoDetalleDTO(producto);
    }
    
    // Mapeador de Entidad a DTO de Resumen
    private ProductoResumenDTO mapToProductoResumenDTO(Producto producto) {
        ProductoResumenDTO dto = new ProductoResumenDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setUrlImagen(producto.getUrlImagen());
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }
        return dto;
    }

    // Mapeador de Entidad a DTO de Detalle
    private ProductoDetalleDTO mapToProductoDetalleDTO(Producto producto) {
        ProductoDetalleDTO dto = new ProductoDetalleDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setUrlImagen(producto.getUrlImagen());
        dto.setFechaCreacion(producto.getFechaCreacion());
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }

        Map<String, Integer> tallasDisponibles = producto.getInventario().stream()
                .collect(Collectors.toMap(
                        inventario -> inventario.getTalla().getNombre(),
                        Inventario::getStock
                ));
        dto.setTallasDisponibles(tallasDisponibles);

        // Obtenemos las reseñas usando el servicio de reseñas
        dto.setReseñas(resenaService.obtenerResenasPorProducto(producto.getId()));
        
        return dto;
    }
}
