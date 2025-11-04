package com.example.toke.services;

import com.example.toke.dto.ProductoAdminDTO;
import com.example.toke.entities.Categoria;
import com.example.toke.entities.Producto;
import com.example.toke.repositories.CategoriaRepository;
import com.example.toke.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final StorageService storageService;

    @Autowired
    public AdminProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, StorageService storageService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
    }

    @Transactional
    public void guardarProducto(ProductoAdminDTO productoDTO) {
        Producto producto;
        if (productoDTO.getId() != null) {
            producto = productoRepository.findById(productoDTO.getId()).orElseThrow();
        } else {
            producto = new Producto();
        }

        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId()).orElseThrow();
        producto.setCategoria(categoria);

        MultipartFile imagen = productoDTO.getImagen();
        if (imagen != null && !imagen.isEmpty()) {
            // Si se sube una nueva imagen, la guardamos y actualizamos la URL
            String nombreArchivo = storageService.store(imagen);
            producto.setUrlImagen("/uploads/products/" + nombreArchivo);
        } else if (producto.getId() == null) {
            // Si es un producto nuevo y no se sube imagen, se podría poner una por defecto
            producto.setUrlImagen("/images/default-product.png"); // Necesitas crear esta imagen
        }

        productoRepository.save(producto);
    }
    
    public void eliminarProducto(Long id) {
        // Aquí también deberías añadir lógica para eliminar la imagen del sistema de archivos.
        productoRepository.deleteById(id);
    }
}