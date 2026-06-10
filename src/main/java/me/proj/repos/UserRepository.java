package me.proj.repos;

import me.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
    extends JpaRepository<User, Long> {
}