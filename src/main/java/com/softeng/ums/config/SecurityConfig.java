package com.softeng.ums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestSecurityHandlers restHandlers;

    public SecurityConfig(RestSecurityHandlers restHandlers) {
        this.restHandlers = restHandlers;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admins/**").hasRole("ADMIN")
                .requestMatchers("/api/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "STUDENT")
                .requestMatchers("/login", "/css/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        restHandlers.authenticationEntryPoint().commence(request, response, authException);
                    } else {
                        response.sendRedirect("/login");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        restHandlers.accessDeniedHandler().handle(request, response, accessDeniedException);
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    }
                })
            );

        return http.build();
    }
}