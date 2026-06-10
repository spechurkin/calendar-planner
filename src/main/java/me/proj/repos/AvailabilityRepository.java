package me.proj.repos;

import me.proj.entities.Availability;
import me.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository
    extends JpaRepository<Availability, Long> {

  Optional<Availability> findByUserAndDate(
      User user,
      LocalDate date
  );

  List<Availability> findAllByDateBetween(
      LocalDate from,
      LocalDate to
  );

  List<Availability> findAllByUser(
      User user
  );

  Collection<Availability> findAllByDate(LocalDate date);
}