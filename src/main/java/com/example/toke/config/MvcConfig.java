package com.example.toke.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cuando una URL empiece con /uploads/, Spring la buscará en la carpeta 'uploads' del proyecto.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}