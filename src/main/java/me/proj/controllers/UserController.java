package me.proj.controllers;

import me.proj.dtos.CreateUserRequest;
import me.proj.entities.User;
import me.proj.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
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
        service.addToProject(projectId, userId);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public void editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.getById(id));
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute CreateUserRequest user,
                         RedirectAttributes redirectAttributes) {
        service.update(id, user);
        redirectAttributes.addAttribute("projectId", user.getProjectId());
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id,
            @RequestParam Long projectId,
            RedirectAttributes redirectAttributes
    ) {
        service.removeFromProject(projectId, id);
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/";
    }
}
