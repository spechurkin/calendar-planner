package me.proj.repos;

import me.proj.entities.Availability;
import me.proj.entities.Project;
import me.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository
        extends JpaRepository<Availability, Long> {

    Optional<Availability> findByProjectAndUserAndDate(
            Project project,
            User user,
            LocalDate date
    );

    List<Availability> findAllByUser(
            User user
    );

    List<Availability> findAllByProjectAndDateIn(Project project, Collection<LocalDate> dates);

    Iterable<? extends Availability> findAllByProjectAndUser(Project project, User user);

    List<Availability> findAllByDateIn(List<LocalDate> dates);

    void deleteByProjectAndUser(Project project, User user);

    boolean existsByProjectAndUserAndDate(Project project, User user, LocalDate date);
}