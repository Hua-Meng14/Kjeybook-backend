package com.bootcamp.bookrentalsystem.config;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                .cors().disable() // Disable CORS
                .csrf().disable() // Disable CSRF protection
                .authorizeRequests()
                .anyRequest()
                .permitAll(); // Allow all requests


                // <-----------------Uncomment to ENABLE CORS----------------->
                // .cors().and() // Enable CORS
                // .csrf()
                // .disable()
                // .authorizeRequests()
                // .requestMatchers(
                //         "/api/v1/auth/**",
                //         "/v2/api-docs",
                //         "/v3/api-docs",
                //         "/v3/api-docs/**",
                //         "/swagger-resources",
                //         "/swagger-resources/**",
                //         "/configuration/ui",
                //         "/configuration/security",
                //         "/swagger-ui/**",
                //         "/webjars/**",
                //         "/swagger-ui.html",
                //         "/api/v1/**")
                // .permitAll()
                // .anyRequest()
                // .authenticated();
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Disable CORS
        configuration.setAllowedOrigins(Arrays.asList("*")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE")); // Update with your allowed methods
        configuration.addAllowedHeader("*"); // Update with your allowed headers
        configuration.setAllowCredentials(false); // Disable credentials

        // <-----------------Uncomment to ENABLE CORS----------------->
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://kjeybook.vercel.app")); // Update with your allowed origins
        // configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE")); // Update with your allowed methods
        // configuration.addAllowedHeader("*"); // Update with your allowed headers
        // configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
