package me.proj.controllers;

import me.proj.dtos.CreateProjectRequest;
import me.proj.services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService service;

  public ProjectController(ProjectService service) {
    this.service = service;
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute CreateProjectRequest project,
      RedirectAttributes redirectAttributes
  ) {
    Long projectId = service.create(project).getId();
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
