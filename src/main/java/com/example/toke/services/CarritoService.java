package com.example.toke.services;


import com.example.toke.dto.CarritoDTO;
import com.example.toke.dto.CarritoItemDTO;
import com.example.toke.entities.Inventario;
import com.example.toke.entities.Producto;
import com.example.toke.entities.Talla;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.exception.StockInsuficienteException;
import com.example.toke.repositories.InventarioRepository;
import com.example.toke.repositories.ProductoRepository;
import com.example.toke.repositories.TallaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {

    public static final String CARRITO_SESSION_KEY = "carrito";

    private final ProductoRepository productoRepository;
    private final TallaRepository tallaRepository;
    private final InventarioRepository inventarioRepository;

    @Autowired
    public CarritoService(ProductoRepository productoRepository, TallaRepository tallaRepository, InventarioRepository inventarioRepository) {
        this.productoRepository = productoRepository;
        this.tallaRepository = tallaRepository;
        this.inventarioRepository = inventarioRepository;
    }

    /**
     * Obtiene el carrito de la sesión actual. Si no existe, crea uno nuevo.
     */
    public CarritoDTO obtenerOCrearCarrito(HttpSession session) {
        CarritoDTO carrito = (CarritoDTO) session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito == null) {
            carrito = new CarritoDTO();
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return carrito;
    }

    /**
     * Agrega un ítem al carrito. Si ya existe, incrementa su cantidad.
     */
    @Transactional(readOnly = true)
    public void agregarItem(HttpSession session, Long productoId, Long tallaId, int cantidad) {
        CarritoDTO carrito = obtenerOCrearCarrito(session);

        Optional<CarritoItemDTO> itemExistente = findItemEnCarrito(carrito, productoId, tallaId);
        Inventario inventario = inventarioRepository.findByProductoIdAndTallaId(productoId, tallaId)
                .orElseThrow(() -> new ProductoNoEncontradoException("No se encontró inventario para este producto y talla."));

        if (itemExistente.isPresent()) {
            // El ítem ya está en el carrito, actualizamos la cantidad
            CarritoItemDTO item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (inventario.getStock() < nuevaCantidad) {
                throw new StockInsuficienteException("Stock insuficiente. Solo quedan " + inventario.getStock() + " unidades.");
            }
            item.setCantidad(nuevaCantidad);
        } else {
            // Es un ítem nuevo
            if (inventario.getStock() < cantidad) {
                throw new StockInsuficienteException("Stock insuficiente. Solo quedan " + inventario.getStock() + " unidades.");
            }
            Producto producto = productoRepository.findById(productoId).orElseThrow();
            Talla talla = tallaRepository.findById(tallaId).orElseThrow();

            CarritoItemDTO nuevoItem = new CarritoItemDTO();
            nuevoItem.setProductoId(productoId);
            nuevoItem.setNombreProducto(producto.getNombre());
            nuevoItem.setTallaId(tallaId);
            nuevoItem.setNombreTalla(talla.getNombre());
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            nuevoItem.setUrlImagen(producto.getUrlImagen());
            nuevoItem.setStockDisponible(inventario.getStock());
            carrito.getItems().add(nuevoItem);
        }
        
        actualizarCarritoEnSesion(session, carrito);
    }

    /**
     * Actualiza la cantidad de un ítem en el carrito. Si la cantidad es 0, lo elimina.
     */
    public void actualizarItem(HttpSession session, Long productoId, Long tallaId, int cantidad) {
        if (cantidad <= 0) {
            eliminarItem(session, productoId, tallaId);
            return;
        }
        CarritoDTO carrito = obtenerOCrearCarrito(session);
        Optional<CarritoItemDTO> itemOpt = findItemEnCarrito(carrito, productoId, tallaId);

        itemOpt.ifPresent(item -> {
             Inventario inventario = inventarioRepository.findByProductoIdAndTallaId(productoId, tallaId).orElseThrow();
             if (inventario.getStock() < cantidad) {
                 throw new StockInsuficienteException("Stock insuficiente. Solo quedan " + inventario.getStock() + " unidades.");
             }
            item.setCantidad(cantidad);
            actualizarCarritoEnSesion(session, carrito);
        });
    }

    /**
     * Elimina un ítem del carrito por completo.
     */
    public void eliminarItem(HttpSession session, Long productoId, Long tallaId) {
        CarritoDTO carrito = obtenerOCrearCarrito(session);
        carrito.getItems().removeIf(item ->
                item.getProductoId().equals(productoId) && item.getTallaId().equals(tallaId)
        );
        actualizarCarritoEnSesion(session, carrito);
    }
    
    private Optional<CarritoItemDTO> findItemEnCarrito(CarritoDTO carrito, Long productoId, Long tallaId) {
        return carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(productoId) && i.getTallaId().equals(tallaId))
                .findFirst();
    }
    
    private void actualizarCarritoEnSesion(HttpSession session, CarritoDTO carrito) {
        carrito.recalcularTotal();
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
}