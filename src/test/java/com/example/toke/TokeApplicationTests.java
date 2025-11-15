package com.example.toke;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers // Habilita la integración con Testcontainers y JUnit 5
class TokeApplicationTests {

    // 1. Define un contenedor de base de datos MySQL.
    // 'mysql:8.0' especifica la imagen de Docker a usar.
    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    // 2. Este método se ejecuta ANTES de que la aplicación se inicie para la prueba.
    // Inyectará las propiedades de conexión del contenedor que acaba de arrancar.
    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        
        // También podemos añadir las propiedades dummy para los otros servicios aquí
        registry.add("mercadopago.access-token", () -> "TEST-DUMMY-TOKEN");
        registry.add("spring.mail.host", () -> "localhost");
    }

    // 3. La prueba original. Ahora sí podrá cargar el contexto.
    @Test
    void contextLoads() {
        // Esta prueba ahora intentará conectarse a la base de datos MySQL temporal
        // que está corriendo en el contenedor de Docker.
    }
}