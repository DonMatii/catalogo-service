package cl.ochodigital.pasteleriamydreams.catalogoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitimos el acceso libre temporalmente para las pruebas
                )
                .csrf(csrf -> csrf.disable()); // Desactivamos CSRF para facilitar el testing local

        return http.build();
    }
}