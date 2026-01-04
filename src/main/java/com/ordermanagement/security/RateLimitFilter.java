package com.ordermanagement.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.contains("/swagger-ui")
            || path.contains("/v3/api-docs");
    }

    private Bucket resolveBucket(String key, int capacity, int minutes) {
        return cache.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.builder()
                    .capacity(capacity)
                    .refillGreedy(capacity, Duration.ofMinutes(minutes))
                    .build();

            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();  
        String ip = request.getRemoteAddr();

        Bucket bucket = null;

        if (path.contains("/auth/login")) {
            bucket = resolveBucket(ip + ":login", 5, 1);
        } 
        else if (path.contains("/search")) {
            bucket = resolveBucket(ip + ":search", 20, 1);
        } 
        else if (path.contains("/admin")) {
            bucket = resolveBucket(ip + ":admin", 30, 1);
        }

        if (bucket != null && !bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                  "message": "Too many requests. Please try later.",
                  "status": 429
                }
            """);
            response.getWriter().flush(); 
            return;
        }

        filterChain.doFilter(request, response);
    }
}
