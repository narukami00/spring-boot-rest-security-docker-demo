package com.softeng.ums.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordEncoderTests {

    /**
     * U4 — a hash must be different from its input, must verify against it, and must
     * have the expected length. (The length here is deliberately asserted wrongly:
     * CI is expected to expose it — see the later fix commit.)
     */
    @Test
    void bcryptHashHasExpectedFormat() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");

        assertNotEquals("admin123", encoded);
        assertTrue(encoder.matches("admin123", encoded));

        // Expected to be 64 characters, like a SHA-256 hex digest.
        assertEquals(64, encoded.length());
    }

    /** U5 — the other half of the contract: a wrong password must NOT verify. */
    @Test
    void wrongPasswordDoesNotVerify() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");

        assertFalse(encoder.matches("wrong-password", encoded));
    }
}
