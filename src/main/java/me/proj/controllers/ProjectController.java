package me.proj.controllers;

import me.proj.dtos.CreateProjectRequest;
import me.proj.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    public ProjectController(
            ProjectService service,
            CurrentUserService currentUserService
    ) {
        this.service = service;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute CreateProjectRequest project,
            RedirectAttributes redirectAttributes
    ) {
        Long projectId = service.create(project).getId();
        service.addUser(projectId, currentUserService.requireCurrentUser().getId());
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id
    ) {
        service.delete(id);
        return "redirect:/";
    }
}
