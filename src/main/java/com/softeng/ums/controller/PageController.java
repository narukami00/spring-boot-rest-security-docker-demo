package com.softeng.ums.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Renders the page shells for all three resources. No repository access here:
 * the browser reads and writes through the REST API (/api/admins, ...), so this
 * controller only supplies the base path, title and (for edit) the record id.
 */
@Controller
public class PageController {

    /** URL segment -> singular noun, used to build titles. Doubles as the whitelist. */
    private static final Map<String, String> RESOURCES = Map.of(
            "admins", "Admin",
            "teachers", "Teacher",
            "students", "Student");

    @GetMapping("/{resource}")
    public String list(@PathVariable String resource, Authentication auth, Model model) {
        if (!RESOURCES.containsKey(resource)) return "redirect:/";
        model.addAttribute("title", RESOURCES.get(resource) + "s");
        model.addAttribute("basePath", resource);
        model.addAttribute("currentUsername", currentUsername(auth));
        return "list";
    }

    @GetMapping("/{resource}/new")
    public String createForm(@PathVariable String resource, Model model) {
        if (!RESOURCES.containsKey(resource)) return "redirect:/";
        return form(model, resource, null, "New ");
    }

    @GetMapping("/{resource}/{id}/edit")
    public String editForm(@PathVariable String resource, @PathVariable Long id, Model model) {
        if (!RESOURCES.containsKey(resource)) return "redirect:/";
        return form(model, resource, id, "Edit ");
    }

    private String form(Model model, String resource, Long id, String verb) {
        model.addAttribute("title", verb + RESOURCES.get(resource));
        model.addAttribute("basePath", resource);
        model.addAttribute("id", id);          // null => create mode
        return "form";
    }

    /** Null-safe: the list page uses this to hide the Delete button on your own row. */
    private String currentUsername(Authentication auth) {
        return (auth == null) ? "" : auth.getName();
    }
}
