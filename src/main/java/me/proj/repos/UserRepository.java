package me.proj.repos;

import me.proj.entities.Project;
import me.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    List<User> findDistinctByProjects(Project project);

    boolean existsByIdAndProjects(Long id, Project project);

    Optional<User> findByName(String name);

    boolean existsByName(String name);

    boolean existsByColor(String color);
}
