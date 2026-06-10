package me.proj.controllers;

import me.proj.dtos.CreateUserRequest;
import me.proj.entities.User;
import me.proj.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public void list(Model model) {
    model.addAttribute("users", service.findAll());
    model.addAttribute("newUser", new User());
  }

  @PostMapping("/create")
  public String create(@ModelAttribute CreateUserRequest user) {
    service.create(user);
    return "redirect:/";
  }

  @GetMapping("/edit/{id}")
  public void editForm(@PathVariable Long id, Model model) {
    model.addAttribute("user", service.getById(id));
  }

  @PostMapping("/edit/{id}")
  public String update(@PathVariable Long id,
                       @ModelAttribute CreateUserRequest user) {
    service.update(id, user);
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