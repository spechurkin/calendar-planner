package me.proj.repos;

import me.proj.entities.Availability;
import me.proj.entities.Project;
import me.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository
    extends JpaRepository<Availability, Long> {

  Optional<Availability> findByProjectAndUserAndDate(
      Project project,
      User user,
      LocalDate date
  );

  List<Availability> findAllByProjectAndDateBetween(
      Project project,
      LocalDate from,
      LocalDate to
  );

  List<Availability> findAllByProjectAndUser(
      Project project,
      User user
  );

  Collection<Availability> findAllByProjectAndDate(
      Project project,
      LocalDate date
  );

  List<Availability> findAllByUser(User user);
}
