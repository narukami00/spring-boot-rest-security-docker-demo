package com.softeng.ums.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

/**
 * U6 — the RESOURCES map in PageController doubles as a whitelist, so a
 * fabricated resource path must redirect home. Plain objects, no Spring context.
 */
class PageControllerTests {

    PageController controller = new PageController();
    Model model = new BindingAwareModelMap();
    TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "n/a", "ROLE_ADMIN");

    @Test
    void unknownResourceRedirectsHome() {
        String view = controller.list("shipdashboard", auth, model);
        assertEquals("redirect:/", view);
    }
}
