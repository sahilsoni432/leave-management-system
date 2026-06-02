package com.microservices.authservice.filter;

import com.microservices.authservice.constants.ApplicationConstants;
import com.microservices.authservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String path = request.getRequestURI();

        if(path.contains("/auth/login")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.contains("/swagger-resources")
                || path.contains("/actuator"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(ApplicationConstants.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith(ApplicationConstants.BEARER))
        {
           String token = authHeader.substring(7);
           if(jwtUtil.validateToken(token))
           {
               String role = jwtUtil.extractRole(token);

               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(null,
                       null, List.of(new SimpleGrantedAuthority(role)));

               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        }

        filterChain.doFilter(request, response);
    }
}
