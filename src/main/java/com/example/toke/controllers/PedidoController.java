package com.example.toke.controllers;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.services.CarritoService;
import com.example.toke.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final CarritoService carritoService;

    @Autowired
    public PedidoController(PedidoService pedidoService, CarritoService carritoService) {
        this.pedidoService = pedidoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/checkout")
    public String mostrarCheckout(HttpSession session, Model model) {
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito"; // No se puede hacer checkout con carrito vacío
        }
        model.addAttribute("carrito", carrito);
        return "pedido/checkout"; // Vista: resources/templates/pedido/checkout.html
    }

    @PostMapping("/realizar-pedido")
    public String realizarPedido(HttpSession session,
                               @RequestParam String direccionEnvio,
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);

        try {
            pedidoService.crearPedido(carrito, userEmail, direccionEnvio);
            session.removeAttribute("carrito"); // Limpiar carrito después de la compra
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorPedido", "Error al procesar el pedido: " + e.getMessage());
            return "redirect:/checkout";
        }

        redirectAttributes.addFlashAttribute("pedidoExitoso", "¡Gracias por tu compra! Tu pedido ha sido realizado con éxito.");
        return "redirect:/"; // O a una página de confirmación de pedido
    }
}
