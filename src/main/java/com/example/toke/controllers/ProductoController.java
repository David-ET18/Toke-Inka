package com.example.toke.controllers;

import com.example.toke.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        return "producto/lista-productos"; // Vista: resources/templates/producto/lista-productos.html
    }

    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerProductoPorId(id));
        return "producto/detalle-producto"; // Vista: resources/templates/producto/detalle-producto.html
    }
}