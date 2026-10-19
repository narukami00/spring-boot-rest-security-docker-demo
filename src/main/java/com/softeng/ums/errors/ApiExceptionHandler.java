package com.softeng.ums.errors;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Catches exceptions the controllers did not handle and turns them into the same
 * JSON error shape a controller would have produced manually.
 *
 * Without this, Spring falls back to the whitelabel error page (HTML), which breaks
 * the JSON contract for API callers while being the correct response for browsers.
 *
 * The split matters: an API client wants a JSON body, a browser wants an HTML page.
 * So this advice answers with JSON only under /api/, and otherwise hands the problem
 * back to the container so the browser still gets the normal error page.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Unknown path, e.g. GET /api/whatever, or a static file that is not there. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> noResource(HttpServletRequest request, HttpServletResponse response,
            NoResourceFoundException ex) throws IOException {
        if (notApi(request)) {
            return delegateToContainer(response, HttpStatus.NOT_FOUND);
        }
        return respond(HttpStatus.NOT_FOUND, ApiError.notFound("The requested resource does not exist"));
    }

    /** Non-numeric id in a path variable, e.g. GET /api/admins/abc */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> badPathVariable(HttpServletRequest request, HttpServletResponse response,
            MethodArgumentTypeMismatchException ex) throws IOException {
        if (notApi(request)) {
            return delegateToContainer(response, HttpStatus.BAD_REQUEST);
        }
        return respond(HttpStatus.BAD_REQUEST,
                ApiError.badRequest("Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'"));
    }

    /** Malformed JSON in the request body. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadableBody(HttpServletRequest request, HttpServletResponse response,
            HttpMessageNotReadableException ex) throws IOException {
        if (notApi(request)) {
            return delegateToContainer(response, HttpStatus.BAD_REQUEST);
        }
        return respond(HttpStatus.BAD_REQUEST, ApiError.badRequest("Request body is missing or is not valid JSON"));
    }

    /** Anything we did not anticipate. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpected(HttpServletRequest request, HttpServletResponse response,
            Exception ex) throws IOException {
        if (notApi(request)) {
            return delegateToContainer(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // The real exception still reaches the log via the servlet container.
        // Returning it to the caller would leak internals such as class names
        // and SQL statements, so the body stays deliberately generic.
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, ApiError.serverError("An unexpected error occurred"));
    }

    private boolean notApi(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    /**
     * Mark the response with a status and let the container's error dispatch render
     * the normal HTML error page. Returning null tells Spring the response is handled.
     */
    private ResponseEntity<ApiError> delegateToContainer(HttpServletResponse response, HttpStatus status)
            throws IOException {
        response.sendError(status.value());
        return null;
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, ApiError body) {
        return ResponseEntity.status(status).body(body);
    }
}
