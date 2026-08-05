package com.fundoonotes.fundoo_notes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // GET AUTHORIZATION HEADER
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // CHECK IF HEADER STARTS WITH "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                // STEP 0 — CHECK BLACKLISTED TOKENS (REDIS SAFE)
                try {
                    if (redisTemplate != null && Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST:" + token))) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token has been logged out");
                        return;
                    }
                } catch (Exception re) {
                    System.err.println("Redis blacklist check warning: " + re.getMessage());
                }

                // STEP 1 — CHECK REDIS CACHE FIRST (REDIS SAFE)
                String cachedEmail = null;
                try {
                    if (redisTemplate != null) {
                        cachedEmail = redisTemplate.opsForValue().get("TOKEN:" + token);
                    }
                } catch (Exception re) {
                    System.err.println("Redis cache read warning: " + re.getMessage());
                }

                if (cachedEmail != null) {
                    email = cachedEmail;
                } else {
                    // STEP 2 — FALLBACK TO JWT PARSING
                    email = jwtUtil.extractEmail(token);

                    if (email != null && jwtUtil.isTokenValid(token)) {
                        try {
                            if (redisTemplate != null) {
                                redisTemplate.opsForValue().set(
                                        "TOKEN:" + token,
                                        email,
                                        24,
                                        TimeUnit.HOURS
                                );
                            }
                        } catch (Exception re) {
                            System.err.println("Redis cache write warning: " + re.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("JWT authentication error: " + e.getMessage());
            }
        }

        // SET AUTHENTICATION IN SECURITY CONTEXT
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email, null, new ArrayList<>()
                    );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}