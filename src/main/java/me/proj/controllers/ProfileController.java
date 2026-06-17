package me.proj.controllers;

import jakarta.validation.Valid;
import me.proj.dtos.UpdateProfileRequest;
import me.proj.security.CurrentUserService;
import me.proj.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute(
                "profile",
                UpdateProfileRequest.from(currentUserService.requireCurrentUser())
        );
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("profile") UpdateProfileRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "profile";
        }

        try {
            userService.updateProfile(
                    currentUserService.requireCurrentUser().getId(),
                    request
            );
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
            return "profile";
        }

        return "redirect:/profile?updated";
    }
}
