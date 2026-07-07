package com.neusoft.elderlycare.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;

/**
 * 直接提供 doc.html 内容的过滤器 — 绕过 ResourceHttpRequestHandler
 */
@Component
public class DocFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if ("/doc.html".equals(req.getRequestURI()) && "GET".equalsIgnoreCase(req.getMethod())) {
            Resource resource = new ClassPathResource("META-INF/resources/doc.html");
            if (resource.exists()) {
                resp.setContentType(MediaType.TEXT_HTML_VALUE);
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(200);
                try (var is = resource.getInputStream()) {
                    StreamUtils.copy(is, resp.getOutputStream());
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
