package com.example.toke.services;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.dto.DetallePedidoDTO;
import com.example.toke.dto.PedidoDetalleDTO;
import com.example.toke.dto.PedidoResumenDTO;
import com.example.toke.entities.*;
import com.example.toke.entities.enums.EstadoPedido;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.exception.StockInsuficienteException;
import com.example.toke.repositories.*;

// Importaciones estándar
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final TallaRepository tallaRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    @Value("${culqi.api.secret-key}")
    private String culqiSecretKey;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, InventarioRepository inventarioRepository, ProductoRepository productoRepository, TallaRepository tallaRepository, EmailService emailService, RestTemplate restTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.tallaRepository = tallaRepository;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    /**
     * MODO DEMO/SIMULACIÓN:
     * Crea el pedido, lo marca como PAGADO inmediatamente y envía el correo.
     * Luego genera la orden en Culqi para que el popup funcione visualmente.
     */
    @Transactional
    public String iniciarProcesoDePago(CarritoDTO carritoDTO, String userEmail, String direccionEnvio) {
        // 1. Obtener Usuario
        Usuario usuario = usuarioRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 2. Crear Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        
        // --- CAMBIO CLAVE PARA QUE FUNCIONE YA ---
        // Marcamos como PAGADO inmediatamente.
        nuevoPedido.setEstado(EstadoPedido.PAGADO); 
        // ----------------------------------------
        
        nuevoPedido.setTotal(carritoDTO.getTotal());
        nuevoPedido.setDireccionEnvio(direccionEnvio);
        
        // 3. Procesar Detalles y (Opcional) Reducir Stock aquí para la demo
        List<DetallePedido> detalles = new ArrayList<>();
        for (var item : carritoDTO.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado. ID: " + item.getProductoId()));
            Talla talla = tallaRepository.findById(item.getTallaId()).orElseThrow(() -> new RuntimeException("Talla no encontrada. ID: " + item.getTallaId()));
            
            // Lógica de Stock (Simplificada para demo: Descontamos ya)
            Inventario inventario = inventarioRepository.findByProductoIdAndTallaId(item.getProductoId(), item.getTallaId())
                    .orElseThrow(() -> new ProductoNoEncontradoException("Inventario no encontrado."));
            
            if (inventario.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para: " + item.getNombreProducto());
            }
            inventario.setStock(inventario.getStock() - item.getCantidad());
            inventarioRepository.save(inventario);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(nuevoPedido);
            detalle.setProducto(producto);
            detalle.setTalla(talla);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalles.add(detalle);
        }
        nuevoPedido.setDetalles(detalles);
        
        // 4. Guardar Pedido en BD
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // --- ENVIAR CORREO INMEDIATAMENTE ---
        try {
            System.out.println("Enviando correo de confirmación a: " + userEmail);
            PedidoDetalleDTO pedidoDTO = mapToPedidoDetalleDTO(pedidoGuardado);
            emailService.enviarBoletaPorCorreo(pedidoDTO, userEmail);
            System.out.println("Correo enviado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al enviar el correo (pero el pedido sigue válido): " + e.getMessage());
            e.printStackTrace();
        }
        // ------------------------------------

        // 5. GENERAR ORDEN CULQI (Para que salga el popup)
        String culqiUrl = "https://api.culqi.com/v2/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(culqiSecretKey);

        long montoEnCentavos = pedidoGuardado.getTotal().multiply(new BigDecimal("100")).longValue();
        String orderNumber = "toke-" + pedidoGuardado.getId() + "-" + System.currentTimeMillis();
        long expiracion = (System.currentTimeMillis() / 1000) + 86400;

        Map<String, Object> clientDetails = new HashMap<>();
        clientDetails.put("first_name", usuario.getNombre());
        clientDetails.put("last_name", usuario.getApellido());
        clientDetails.put("email", usuario.getEmail());
        clientDetails.put("phone_number", usuario.getTelefono() != null ? usuario.getTelefono() : "999999999");

        Map<String, Object> body = new HashMap<>();
        body.put("amount", montoEnCentavos);
        body.put("currency_code", "PEN");
        body.put("description", "Pedido #" + pedidoGuardado.getId());
        body.put("order_number", orderNumber);
        body.put("expiration_date", expiracion);
        body.put("client_details", clientDetails);
        body.put("confirm", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            JsonNode response = restTemplate.postForObject(culqiUrl, request, JsonNode.class);
            if (response != null && response.has("id")) {
                return response.get("id").asText();
            } else {
                // Fallback por si falla Culqi, devolvemos un string dummy para que no rompa el front
                // (Aunque en este punto el pedido ya está guardado y pagado en tu sistema)
                System.err.println("Culqi no devolvió ID, pero el pedido se guardó localmente.");
                return "ord_dummy_error"; 
            }
        } catch (Exception e) {
            System.err.println("Error conectando con Culqi: " + e.getMessage());
            // Devolvemos dummy para que no explote, el pedido ya existe en tu BD
            return "ord_dummy_error";
        }
    }
    
    // Este método queda por compatibilidad, pero en este modo demo ya hicimos todo arriba.
    @Transactional
    public void confirmarPedidoPagado(Long pedidoId) {
        // Lógica vacía o de seguridad extra si llega el webhook tarde.
        System.out.println("Webhook recibido para pedido " + pedidoId + " (Ya fue procesado en modo demo).");
    }
    
    // --- MÉTODOS DE LECTURA (Sin cambios) ---
    
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId).stream().map(this::mapToPedidoResumenDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoDetalleDTO obtenerDetallePedidoParaCliente(Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("No tienes permiso para ver este pedido.");
        }
        return mapToPedidoDetalleDTO(pedido);
    }

    private PedidoResumenDTO mapToPedidoResumenDTO(Pedido pedido) {
        PedidoResumenDTO dto = new PedidoResumenDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setNumeroDeItems(pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum());
        return dto;
    }
    
    private PedidoDetalleDTO mapToPedidoDetalleDTO(Pedido pedido) {
        PedidoDetalleDTO dto = new PedidoDetalleDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());
        if (pedido.getUsuario() != null) {
            dto.setNombreCliente(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
        }
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