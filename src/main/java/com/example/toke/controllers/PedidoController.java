package com.example.toke.controllers;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.services.CarritoService;
import com.example.toke.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final CarritoService carritoService;

    // Inyectamos la llave pública desde application.properties
    @Value("${culqi.api.public-key}")
    private String culqiPublicKey;

    @Autowired
    public PedidoController(PedidoService pedidoService, CarritoService carritoService) {
        this.pedidoService = pedidoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/checkout")
    public String mostrarCheckout(HttpSession session, Model model) {
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Enviamos el carrito y la llave pública a la vista
        model.addAttribute("carrito", carrito);
        model.addAttribute("culqiPublicKey", culqiPublicKey);
        
        return "pedido/checkout";
    }

    @PostMapping("/realizar-pedido")
    @ResponseBody // Retorna JSON, no una vista HTML
    public ResponseEntity<?> realizarPedido(HttpSession session,
                                            @RequestParam String direccionEnvio) {
        // 1. Verificar autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
             return ResponseEntity.status(401).body(Map.of("error", "Usuario no autenticado. Por favor, inicie sesión."));
        }
        String userEmail = auth.getName();
        
        // 2. Verificar carrito
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
        if (carrito.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El carrito está vacío."));
        }

        // 3. Crear la orden (Llamada al servicio que usa RestTemplate)
        try {
            String culqiOrderId = pedidoService.iniciarProcesoDePago(carrito, userEmail, direccionEnvio);
            
            // Limpiamos el carrito solo si se generó la orden con éxito
            session.removeAttribute("carrito");
            
            System.out.println("Orden generada con éxito. ID Culqi: " + culqiOrderId);
            
            // Devolvemos el ID al frontend para que abra el popup
            return ResponseEntity.ok(Map.of("culqiOrderId", culqiOrderId));

        } catch (Exception e) {
            System.err.println("ERROR en PedidoController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al procesar el pedido. Intente nuevamente."));
        }
    }
    
}