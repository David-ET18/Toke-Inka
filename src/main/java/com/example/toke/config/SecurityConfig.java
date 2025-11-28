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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // --- REGLAS PÚBLICAS ---
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/productos", "/producto/**").permitAll()
                .requestMatchers("/register", "/login", "/logout").permitAll()

                // NUEVO: Permitir que Culqi nos envíe notificaciones sin estar logueado
                .requestMatchers("/api/webhooks/**").permitAll()

                // --- REGLAS PARA CLIENTES ---
                // /realizar-pedido requiere ROL CLIENTE y el Token CSRF (que ya pusimos en el HTML)
                .requestMatchers("/carrito/**", "/checkout", "/realizar-pedido", "/mi-cuenta/**", "/pedidos/**").hasRole("CLIENTE")

                // --- REGLAS PARA ADMINISTRADORES ---
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // --- REGLA POR DEFECTO ---
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
            )
            // NUEVO: Configuración CSRF para permitir el Webhook
            .csrf(csrf -> csrf
                // Desactivamos la protección CSRF SOLO para las notificaciones de Culqi
                // porque ellos envían un POST desde un servidor externo.
                // Para el resto (/realizar-pedido), la protección sigue activa y segura.
                .ignoringRequestMatchers("/api/webhooks/**")
            );

        return http.build();
    }
}