package com.example.toke.controllers;
import com.example.toke.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoService productoService;

    @Autowired
    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        // Por ejemplo, mostramos todos los productos en la página principal
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        return "index"; // Vista: resources/templates/index.html
    }
}