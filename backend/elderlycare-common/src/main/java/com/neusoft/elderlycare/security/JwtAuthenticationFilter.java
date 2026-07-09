package com.neusoft.elderlycare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests.
 * This filter extends OncePerRequestFilter to ensure it's executed only once per request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * Provider for JWT token operations
     */
    private final JwtTokenProvider tokenProvider;
    /**
     * Service for loading user-specific data
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Core method that processes the JWT token from the request header.
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The chain of filters to pass the request through
     * @throws ServletException If a servlet-related exception occurs
     * @throws IOException If an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Extract the Authorization header from the request
        String header = request.getHeader("Authorization");
        // Check if header exists and starts with "Bearer "
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            // Extract the token from the header (removing "Bearer " prefix)
            String token = header.substring(7);
            // Validate the token
            if (tokenProvider.validate(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(tokenProvider.getPhone(token));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
