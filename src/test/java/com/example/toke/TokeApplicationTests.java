package com.example.toke;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // <-- ¡AÑADE ESTE IMPORT!

@SpringBootTest
@ActiveProfiles("test") // <-- ¡AÑADE ESTA ANOTACIÓN!
class TokeApplicationTests {

    // Ya no necesitamos Testcontainers, así que eliminamos todo ese código.

    @Test
    void contextLoads() {
        // Esta prueba ahora se ejecutará forzando el perfil 'test'
    }
}