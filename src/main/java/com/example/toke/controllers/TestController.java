package com.example.toke.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String showTestPage() {
        // Esta ruta devolverá nuestra nueva página de prueba.
        return "test/test-page";
    }
}
