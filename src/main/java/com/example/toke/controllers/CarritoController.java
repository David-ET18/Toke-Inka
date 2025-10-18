package com.example.toke.controllers;

import com.example.toke.dto.CarritoDTO;
import com.example.toke.services.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Importante
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Importante

import java.util.Map;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    @Autowired
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Este método sigue igual, muestra la página principal del carrito
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
        model.addAttribute("carrito", carrito);
        return "carrito/vista-carrito";
    }

    // Mantenemos el método original por si JavaScript falla
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId,
                                 @RequestParam Long tallaId,
                                 @RequestParam(defaultValue = "1") int cantidad,
                                 HttpSession session) {
        carritoService.agregarItem(session, productoId, tallaId, cantidad);
        return "redirect:/carrito";
    }

    // --- NUEVOS MÉTODOS PARA AJAX ---

    @PutMapping("/actualizar") // Usamos PUT para actualizar
    @ResponseBody // Indica a Spring que la respuesta es el cuerpo, no el nombre de una vista
    public ResponseEntity<?> actualizarCantidadAjax(@RequestParam Long productoId,
                                                    @RequestParam Long tallaId,
                                                    @RequestParam int cantidad,
                                                    HttpSession session) {
        try {
            carritoService.actualizarItem(session, productoId, tallaId, cantidad);
            CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
            // Devolvemos el estado actualizado del carrito
            return ResponseEntity.ok(Map.of(
                "total", carrito.getTotal(),
                "totalItems", carrito.getItems().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar") // Usamos DELETE para eliminar
    @ResponseBody
    public ResponseEntity<?> eliminarDelCarritoAjax(@RequestParam Long productoId,
                                                    @RequestParam Long tallaId,
                                                    HttpSession session) {
        carritoService.eliminarItem(session, productoId, tallaId);
        CarritoDTO carrito = carritoService.obtenerOCrearCarrito(session);
        // Devolvemos el estado actualizado del carrito
        return ResponseEntity.ok(Map.of(
            "total", carrito.getTotal(),
            "totalItems", carrito.getItems().size()
        ));
    }
}