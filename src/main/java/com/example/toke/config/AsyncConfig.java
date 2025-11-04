package com.example.toke.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync // Habilita la capacidad de Spring para ejecutar tareas en hilos separados
public class AsyncConfig {
}