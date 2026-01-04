package com.ordermanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordermanagement.service.UserService;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserService userService;
        private final ObjectMapper objectMapper;
        private final RateLimitFilter rateLimitFilter;
    

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
{

    http.csrf(csrf -> csrf.disable())
     .formLogin(form -> form.disable())    
        .httpBasic(basic -> basic.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                   "/api/v1/auth/login",
                    "/api/v1/users/register",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(
            new JwtAuthFilter(userService),
            UsernamePasswordAuthenticationFilter.class
        )
        .addFilterAfter(rateLimitFilter, JwtAuthFilter.class)
        .exceptionHandling(e -> e
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
            .accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper))
        );

    return http.build();
}


}

