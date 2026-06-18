package me.proj.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 7, unique = true)
    private String color;

    @Column
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<UserRole> roles = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private List<Project> projects = new ArrayList<>();

    public User(String name, String color) {
        this.name = name;
        this.color = color;
        addRole(UserRole.PLAYER);
    }

    public Set<UserRole> getRoles() {
        ensureDefaultRole();
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles == null
                ? new HashSet<>()
                : new HashSet<>(roles);
        ensureDefaultRole();
    }

    public boolean hasRole(UserRole role) {
        return getRoles().contains(role);
    }

    public boolean canManageProjects() {
        return hasRole(UserRole.MASTER) || hasRole(UserRole.ADMIN);
    }

    public boolean canManageRoles() {
        return hasRole(UserRole.ADMIN);
    }

    public boolean isPlayer() {
        return hasRole(UserRole.PLAYER);
    }

    public boolean isMaster() {
        return hasRole(UserRole.MASTER);
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    public List<UserRole> getSortedRoles() {
        return getRoles()
                .stream()
                .sorted()
                .toList();
    }

    public void addRole(UserRole role) {
        getRoles().add(role);
    }

    public void removeRole(UserRole role) {
        if (role == UserRole.PLAYER) {
            return;
        }

        getRoles().remove(role);
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void ensureDefaultRole() {
        if (roles == null) {
            roles = new HashSet<>();
        }

        if (roles.isEmpty()) {
            roles.add(UserRole.PLAYER);
        }
    }
}
