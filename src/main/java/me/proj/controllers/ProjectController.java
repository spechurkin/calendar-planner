package me.proj.controllers;

import me.proj.dtos.CreateProjectRequest;
import me.proj.entities.User;
import me.proj.security.AuthorizationService;
import me.proj.services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    private final AuthorizationService authorizationService;

    public ProjectController(
            ProjectService service,
            AuthorizationService authorizationService
    ) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute CreateProjectRequest project,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = authorizationService.requireProjectManager();
        Long projectId = service.create(project).getId();
        service.addUser(projectId, currentUser.getId());
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id
    ) {
        authorizationService.requireProjectManager();
        service.delete(id);
        return "redirect:/";
    }
}
