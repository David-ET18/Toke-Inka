package com.example.toke.services;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.dto.DetallePedidoDTO;
import com.example.toke.dto.PedidoDetalleDTO;
import com.example.toke.dto.PedidoResumenDTO;
import com.example.toke.services.EmailService;
import com.example.toke.entities.*;
import com.example.toke.entities.enums.EstadoPedido;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.exception.StockInsuficienteException;
import com.example.toke.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final EmailService emailService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, InventarioRepository inventarioRepository,EmailService emailService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.emailService = emailService;
    }

@Transactional
public Pedido crearPedido(CarritoDTO carritoDTO, String userEmail, String direccionEnvio) {
    // === INICIO DE TU CÓDIGO ORIGINAL (SIN CAMBIOS) ===
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
    // === FIN DE TU CÓDIGO ORIGINAL ===

    // 1. Guarda el pedido en la base de datos para obtener un ID.
    Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

    // 2. Después de guardar, inicia el envío del correo de confirmación.
    try {
        // Convierte la entidad Pedido (con todos sus datos) a un DTO adecuado para los servicios.
        PedidoDetalleDTO pedidoDTO = mapToPedidoDetalleDTO(pedidoGuardado);
        
        // Llama al servicio de correo. Gracias a @Async, esta llamada no bloqueará la respuesta al usuario.
        emailService.enviarBoletaPorCorreo(pedidoDTO, userEmail);

    } catch (Exception e) {
        // Capturamos cualquier excepción (ej. si el servidor de correo falla)
        // para que la compra del usuario NO FALLE. El pedido ya está guardado.
        // En un entorno de producción, aquí registrarías el error en un log (ej. con SLF4J).
        System.err.println("ADVERTENCIA: El pedido #" + pedidoGuardado.getId() + " se creó correctamente, " +
                           "pero falló el envío del correo de confirmación a " + userEmail + ". " +
                           "Causa: " + e.getMessage());
    }

    // 3. Devuelve el pedido guardado al controlador.
    return pedidoGuardado;
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
    @Transactional(readOnly = true)
public PedidoDetalleDTO obtenerDetallePedidoParaCliente(Long pedidoId, Long usuarioId) {
    Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    
    // ¡Comprobación de seguridad crucial!
    // Asegura que un usuario solo pueda ver sus propios pedidos.
    if (!pedido.getUsuario().getId().equals(usuarioId)) {
        throw new AccessDeniedException("No tienes permiso para ver este pedido.");
    }
    
    // Si la comprobación pasa, mapeamos y devolvemos el DTO.
    // Necesitarás crear un mapper para PedidoDetalleDTO, similar a los del AdminPedidoService.
    return mapToPedidoDetalleDTO(pedido); // Reutiliza un mapper si ya lo tienes.
}
 private PedidoDetalleDTO mapToPedidoDetalleDTO(Pedido pedido) {
        PedidoDetalleDTO dto = new PedidoDetalleDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());
        // Puedes añadir ciudad, país, etc., si los tienes en la entidad Pedido
        
        if (pedido.getUsuario() != null) {
            dto.setNombreCliente(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
        }

        // Traduce la lista de Entidades DetallePedido a una lista de DetallePedidoDTO
        List<DetallePedidoDTO> detallesDTO = pedido.getDetalles().stream().map(detalle -> {
            DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
            detalleDTO.setProductoId(detalle.getProducto().getId());
            detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
            detalleDTO.setUrlImagen(detalle.getProducto().getUrlImagen());
            detalleDTO.setNombreTalla(detalle.getTalla().getNombre());
            detalleDTO.setCantidad(detalle.getCantidad());
            detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            return detalleDTO;
        }).collect(Collectors.toList());
        
        dto.setDetalles(detallesDTO);

        return dto;
    }
}
