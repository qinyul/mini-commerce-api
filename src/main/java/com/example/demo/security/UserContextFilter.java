package com.example.demo.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest req = (HttpServletRequest) request;

            String userId = req.getHeader("x-user-id");

            if (userId != null && !userId.isBlank()) {
                AuditContext.setCurrentUser(userId);
            }

            chain.doFilter(request, response);
        } finally {
            AuditContext.clear();
        }

    }
}
