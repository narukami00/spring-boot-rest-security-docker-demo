package com.softeng.ums.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

class AdminEntityTests {

    /** U1 — the full-args constructor populates every field; JPA owns the id, so it stays null. */
    @Test
    void constructorPopulatesAllFieldsAndKeepsIdNull() {
        Admin admin = new Admin("Admin User", "admin@uni.com", "admin", "admin123", "ROLE_ADMIN");

        assertEquals("Admin User", admin.getName());
        assertEquals("admin@uni.com", admin.getEmail());
        assertEquals("admin", admin.getUsername());
        assertEquals("admin123", admin.getPassword());
        assertEquals("ROLE_ADMIN", admin.getRole());
        assertNull(admin.getId());
    }

    /** U2 — WRITE_ONLY: the API accepts a password on input but must never emit one. */
    @Test
    void passwordIsWriteOnlyInJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Admin admin = new Admin("Admin User", "admin@uni.com", "admin", "admin123", "ROLE_ADMIN");

        // Serialisation: the hash must not leak into a response.
        String json = mapper.writeValueAsString(admin);
        assertFalse(json.contains("password"), "password must never be serialized: " + json);
        assertTrue(json.contains("admin"), "other fields stay visible");

        // Deserialisation: a request body may still supply one (for create/update).
        Admin deserialized = mapper.readValue(
                "{\"name\":\"A\",\"email\":\"a@uni.com\",\"username\":\"a\",\"password\":\"secret\",\"role\":\"ROLE_ADMIN\"}",
                Admin.class);
        assertEquals("secret", deserialized.getPassword());
    }
}
