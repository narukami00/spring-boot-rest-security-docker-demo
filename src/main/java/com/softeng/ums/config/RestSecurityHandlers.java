package com.softeng.ums.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestSecurityHandlers {

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) -> write(response, HttpServletResponse.SC_UNAUTHORIZED,
                "unauthenticated", "Authentication required");
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException accessDeniedException) -> {
            if (isCsrfFailure(accessDeniedException)) {
                write(response, HttpServletResponse.SC_FORBIDDEN, "csrf",
                        "Missing or invalid CSRF token. Send the token in the X-CSRF-TOKEN header.");
            } else {
                write(response, HttpServletResponse.SC_FORBIDDEN, "forbidden",
                        "You do not have permission to access this resource");
            }
        };
    }

    /**
     * A CSRF rejection and a role rejection both arrive as an AccessDeniedException,
     * but only the cause chain distinguishes them. Without this, a missing token is
     * reported as a permission problem, which sends the caller looking in the wrong place.
     */
    private boolean isCsrfFailure(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof CsrfException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void write(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status, error, message));
    }
}
