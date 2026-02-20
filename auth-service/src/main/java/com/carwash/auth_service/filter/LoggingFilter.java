package com.carwash.auth_service.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

@Component
@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request and response to read body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest,10);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            logRequest(wrappedRequest);

            // Continue with the request
            chain.doFilter(wrappedRequest, wrappedResponse);

            // Log response
            logResponse(wrappedResponse, System.currentTimeMillis() - startTime);

            // Copy cached body to actual response
            wrappedResponse.copyBodyToResponse();

        } catch (Exception e) {
            log.error("❌ Exception in filter: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) throws UnsupportedEncodingException {
        log.info("╔════════════════════════════════════════════════════════════");
        log.info("║ 📥 INCOMING REQUEST");
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ Method      : {}", request.getMethod());
        log.info("║ URI         : {}", request.getRequestURI());
        log.info("║ Query String: {}", request.getQueryString());
        log.info("║ Remote Addr : {}", request.getRemoteAddr());
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ HEADERS:");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // Mask sensitive headers
            if (headerName.equalsIgnoreCase("authorization")) {
                headerValue = maskToken(headerValue);
            }

            log.info("║   {} = {}", headerName, headerValue);
        }

        // Log request body
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            log.info("╠════════════════════════════════════════════════════════════");
            log.info("║ REQUEST BODY:");
            String body = new String(content, request.getCharacterEncoding());
            log.info("║ {}", body);
        }

        log.info("╚════════════════════════════════════════════════════════════");
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) throws UnsupportedEncodingException {
        log.info("╔════════════════════════════════════════════════════════════");
        log.info("║ 📤 OUTGOING RESPONSE");
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ Status      : {} {}", response.getStatus(), getStatusText(response.getStatus()));
        log.info("║ Duration    : {} ms", duration);
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ HEADERS:");

        response.getHeaderNames().forEach(headerName -> {
            String headerValue = response.getHeader(headerName);
            log.info("║   {} = {}", headerName, headerValue);
        });

        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            log.info("╠════════════════════════════════════════════════════════════");
            log.info("║ RESPONSE BODY:");
            String body = new String(content, response.getCharacterEncoding());
            log.info("║ {}", body);
        }

        log.info("╚════════════════════════════════════════════════════════════");
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 7) + "..." + token.substring(token.length() - 4);
    }

    private String getStatusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "CREATED";
            case 204 -> "NO CONTENT";
            case 400 -> "BAD REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT FOUND";
            case 500 -> "INTERNAL SERVER ERROR";
            default -> "";
        };
    }
}