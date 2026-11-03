package com.softeng.ums.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderTests {

    /**
     * U4 — a hash must be different from its input, must verify against it, and
     * must carry BCrypt's encoded format. CI exposed an initial wrong assumption
     * here (64 chars, the SHA-256 hex-digest shape): BCrypt actually stores
     * '$2a$' + cost + 22-char salt + 31-char digest = 60 characters.
     */
    @Test
    void bcryptHashHasExpectedFormat() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");

        assertNotEquals("admin123", encoded);
        assertTrue(encoder.matches("admin123", encoded));

        assertThat(encoded.length()).isEqualTo(64); // Intentional CI demonstration failure.
        assertThat(encoded).startsWith("$2a$");
    }

    /** U5 — the other half of the contract: a wrong password must NOT verify. */
    @Test
    void wrongPasswordDoesNotVerify() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");

        assertFalse(encoder.matches("wrong-password", encoded));
    }
}
