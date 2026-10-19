package com.softeng.ums.errors;

/**
 * Standard error body for every non-2xx response from the API.
 * All responses should look the same shape, whichever layer produced them:
 * a controller, the security filter chain, or the exception handler below.
 */
public record ApiError(int status, String error, String message) {

    public static ApiError notFound(String message) {
        return new ApiError(404, "not_found", message);
    }

    public static ApiError badRequest(String message) {
        return new ApiError(400, "bad_request", message);
    }

    public static ApiError serverError(String message) {
        return new ApiError(500, "server_error", message);
    }
}
