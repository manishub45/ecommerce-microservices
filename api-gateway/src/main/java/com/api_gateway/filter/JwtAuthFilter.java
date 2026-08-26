package com.api_gateway.filter;

import com.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final Map<String, String> PROTECTED_PATHS = new LinkedHashMap<>();

    static {
        PROTECTED_PATHS.put("/api/v1/product/add", "ADMIN");
        PROTECTED_PATHS.put("/api/v1/product/upload-image", "ADMIN");
        PROTECTED_PATHS.put("/api/v1/order", "CUSTOMER");
        PROTECTED_PATHS.put("/api/v1/payment", "CUSTOMER");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        String requiredRole = null;
        for (Map.Entry<String, String> entry : PROTECTED_PATHS.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                requiredRole = entry.getValue();
                break;
            }
        }

        if (requiredRole == null) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtUtil.validateAndExtractClaims(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        String userRole = claims.get("role", String.class);

        if (!requiredRole.equals(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("You do not have permission to access this resource");
            return;
        }

        request.setAttribute("email", claims.getSubject());
        request.setAttribute("role", userRole);

        chain.doFilter(request, response);
    }
}