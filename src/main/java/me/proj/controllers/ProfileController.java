package me.proj.controllers;

import jakarta.validation.Valid;
import me.proj.dtos.UpdateProfileRequest;
import me.proj.entities.User;
import me.proj.security.CurrentUserService;
import me.proj.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final CurrentUserService currentUserService;
    private final UserService userService;

    public ProfileController(
            CurrentUserService currentUserService,
            UserService userService
    ) {
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = currentUserService.requireCurrentUser();

        model.addAttribute("user", currentUser);
        model.addAttribute("title", "Редактирование профиля");

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute UpdateProfileRequest request,
                                RedirectAttributes redirectAttributes) {
        User currentUser = currentUserService.requireCurrentUser();

        try {
            userService.updateProfile(currentUser.getId(), request);
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }
}
