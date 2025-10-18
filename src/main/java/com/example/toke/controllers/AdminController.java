package com.example.toke.controllers;

import com.example.toke.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Todas las rutas aquí dentro requerirán ROLE_ADMIN por SecurityConfig
public class AdminController {

    private final DashboardService dashboardService;

    @Autowired
    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("estadisticas", dashboardService.obtenerEstadisticas());
        return "admin/dashboard"; // Vista: resources/templates/admin/dashboard.html
    }
    
    // Aquí irían más métodos para gestionar productos, pedidos, usuarios, etc.
    // Ejemplo:
    // @GetMapping("/productos")
    // public String gestionarProductos(Model model) { ... }
}