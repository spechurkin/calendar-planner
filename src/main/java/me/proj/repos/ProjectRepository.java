package me.proj.repos;

import me.proj.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository
    extends JpaRepository<Project, Long> {
}
