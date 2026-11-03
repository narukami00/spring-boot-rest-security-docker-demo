package com.softeng.ums.it;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * I1 — the whole Spring context must boot against a real MySQL 8.4 container,
 * and the DataSeeder must have created the demo admin in it.
 *
 * The @SpringBootTest / @AutoConfigureMockMvc / MockMvc configuration lives in
 * IntegrationBase — redeclaring them here would override the base annotation
 * and change the shared context configuration.
 */
@WithMockUser(roles = "ADMIN")
class ApplicationIT extends IntegrationBase {

    @Test
    void contextBootsAgainstContainerizedMysqlAndSeederRan() throws Exception {
        mvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItem("admin")));
    }
}

