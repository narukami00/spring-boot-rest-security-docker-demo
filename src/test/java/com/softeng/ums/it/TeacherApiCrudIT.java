package com.softeng.ums.it;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

/**
 * I2 + I3 — full CRUD round trip through the real controller, security filter
 * chain (CSRF included) and the containerized MySQL. MockMvc is inherited from
 * IntegrationBase.
 */
@WithMockUser(roles = "ADMIN")
class TeacherApiCrudIT extends IntegrationBase {

    private static final String TEACHER_JSON =
            "{\"name\":\"Fahim\",\"email\":\"fahim@uni.com\",\"username\":\"fahim\",\"password\":\"fahim123\"}";

    /** I2 — POST creates (201 + Location), and a GET on that Location returns the record. */
    @Test
    void createTeacherReturns201ThenGetByIdReturns200() throws Exception {
        MvcResult created = mvc.perform(post("/api/teachers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEACHER_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("fahim"))
                .andExpect(jsonPath("$.email").value("fahim@uni.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    /** I3 — PUT updates. DELETE then completes the round trip. */
    @Test
    void updateThenDeleteTeacher() throws Exception {
        MvcResult created = mvc.perform(post("/api/teachers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEACHER_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");

        mvc.perform(put(location)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fahim Updated\",\"email\":\"fahim@uni.com\",\"username\":\"fahim\",\"password\":\"newpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fahim Updated"));

        // CI exposed the wrong guess here (expected 200): REST DELETE answers
        // 204 No Content — the record is gone and there is nothing to return.
        mvc.perform(delete(location).with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
