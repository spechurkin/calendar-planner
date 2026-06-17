package me.proj.controllers;

import jakarta.validation.Valid;
import me.proj.dtos.SignupRequest;
import me.proj.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signup", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signup") SignupRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            authService.register(request);
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
            return "signup";
        }

        return "redirect:/login?registered";
    }
}
