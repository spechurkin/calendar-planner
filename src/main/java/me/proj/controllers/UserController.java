package me.proj.controllers;

import me.proj.dtos.CreateUserRequest;
import me.proj.dtos.UpdateProfileRequest;
import me.proj.entities.User;
import me.proj.security.AuthorizationService;
import me.proj.security.CurrentUserService;
import me.proj.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final CurrentUserService currentUserService;
    private final AuthorizationService authorizationService;

    public UserController(
            UserService service,
            CurrentUserService currentUserService,
            AuthorizationService authorizationService
    ) {
        this.service = service;
        this.currentUserService = currentUserService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public void list(
            @RequestParam Long projectId,
            Model model
    ) {
        model.addAttribute("users", service.findAllByProject(projectId));
        model.addAttribute("newUser", new User());
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute CreateUserRequest user,
            RedirectAttributes redirectAttributes
    ) {
        authorizationService.requireProjectManager();
        service.create(user);
        redirectAttributes.addAttribute("projectId", user.getProjectId());
        return "redirect:/";
    }

    @PostMapping("/add-to-project")
    public String addToProject(
            @RequestParam Long projectId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes
    ) {
        authorizationService.requireProjectManager();
        service.addToProject(projectId, userId);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @PostMapping("/join-project")
    public String joinProject(
            @RequestParam Long projectId,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = currentUserService.requireCurrentUser();
        service.joinProject(currentUser.getId(), projectId);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @PostMapping("/{id}/roles")
    public String updateRoles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean master,
            @RequestParam Long projectId,
            RedirectAttributes redirectAttributes
    ) {
        authorizationService.requireRoleManager();
        service.updateMasterRole(id, master);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id,
            @RequestParam Long projectId,
            RedirectAttributes redirectAttributes
    ) {
        authorizationService.requireProjectManager();
        service.removeFromProject(projectId, id);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }
}
