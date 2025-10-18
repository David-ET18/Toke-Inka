package com.example.toke.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para encriptar y verificar contraseñas.
     * Usamos BCrypt, el estándar de la industria.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad que protege las rutas HTTP.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            // --- REGLAS PÚBLICAS (LA CORRECCIÓN ESTÁ AQUÍ) ---
            // Permite el acceso sin autenticación a todos los recursos estáticos.
            // Esta es la parte más importante que faltaba.
            .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
            
            // Permite el acceso a las páginas públicas
            .requestMatchers("/", "/productos", "/producto/**").permitAll()
            .requestMatchers("/register", "/login", "/logout").permitAll()

            // --- REGLAS PARA CLIENTES ---
            // Solo usuarios con rol CLIENTE pueden acceder a estas rutas.
            // NOTA: Spring Security usa hasRole(), que espera el nombre sin el prefijo "ROLE_".
            .requestMatchers("/carrito/**", "/checkout", "/realizar-pedido", "/mi-cuenta/**", "/pedidos/**").hasRole("CLIENTE")

            // --- REGLAS PARA ADMINISTRADORES ---
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // --- REGLA POR DEFECTO ---
            // Cualquier otra petición que no coincida con las anteriores, requiere autenticación.
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

    return http.build();
}
}