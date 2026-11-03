package com.softeng.ums.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * U3 — every layer (controllers, exception advice, security handlers) reports errors
 * through this record, so the factory-to-status mapping IS the API error contract.
 */
class ApiErrorTests {

    @Test
    void factoriesProduceCorrectStatusAndSlug() {
        assertEquals(404, ApiError.notFound("nope").status());
        assertEquals("not_found", ApiError.notFound("nope").error());
        assertEquals("nope", ApiError.notFound("nope").message());

        assertEquals(400, ApiError.badRequest("bad input").status());
        assertEquals("bad_request", ApiError.badRequest("bad input").error());

        assertEquals(403, ApiError.forbidden("not allowed").status());
        assertEquals("forbidden", ApiError.forbidden("not allowed").error());

        assertEquals(500, ApiError.serverError("boom").status());
        assertEquals("server_error", ApiError.serverError("boom").error());
    }
}
