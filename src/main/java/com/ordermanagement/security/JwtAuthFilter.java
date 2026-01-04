package com.ordermanagement.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ordermanagement.entity.User;
import com.ordermanagement.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

    String path = request.getServletPath();

      return path.equals("/auth/login")
        || path.equals("/users/register")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
}


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull  FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            String username = JwtValidator.extractUsername(token);

            
            User user = userService.getUserByUsername(username);
            
            var authorities= List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name() ));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,  authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

        }
        filterChain.doFilter(request, response);
    }

}
