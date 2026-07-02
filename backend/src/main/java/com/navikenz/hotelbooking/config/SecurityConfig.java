package com.navikenz.hotelbooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navikenz.hotelbooking.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v3/api-docs", "/v3/api-docs/**",
                "/api-docs", "/api-docs/**",
                "/swagger-ui/**", "/swagger-ui.html");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/auth/**", "/h2-console/**",
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs", "/v3/api-docs/**", "/api-docs", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hotels", "/hotels/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms", "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/hotels", "/rooms").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/hotels/{id}", "/rooms/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/hotels/{id}", "/rooms/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/bookings/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/bookings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/bookings/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/bookings").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/bookings/{id}/cancel").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/bookings/{id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/payments/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/payments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJsonError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJsonError(response, HttpStatus.FORBIDDEN, "Admin access required")));

        return http.build();
    }

    private void writeJsonError(jakarta.servlet.http.HttpServletResponse response, HttpStatus status, String detail)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("detail", detail);
        objectMapper.writeValue(response.getWriter(), body);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
