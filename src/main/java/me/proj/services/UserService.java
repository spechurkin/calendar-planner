package me.proj.services;

import me.proj.dtos.CreateUserRequest;
import me.proj.entities.User;
import me.proj.repos.AvailabilityRepository;
import me.proj.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final AvailabilityRepository availabilityRepository;

  public UserService(
      UserRepository userRepository,
      AvailabilityRepository availabilityRepository
  ) {
    this.userRepository = userRepository;
    this.availabilityRepository = availabilityRepository;
  }

  public User create(CreateUserRequest request) {
    User user = new User();

    user.setName(request.getName());
    user.setColor(request.getColor());

    return userRepository.save(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User getById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() ->
            new RuntimeException("User not found")
        );
  }

  public void update(Long id, CreateUserRequest updated) {
    User user = getById(id);

    user.setName(updated.getName());
    user.setColor(updated.getColor());

    userRepository.save(user);
  }

  public void delete(Long id) {
    var user =
        userRepository.findById(id)
            .orElse(null);

    if (user == null) {
      return;
    }

    availabilityRepository.deleteAll(
        availabilityRepository.findAll()
            .stream()
            .filter(a -> a.getUser().getId().equals(id))
            .toList()
    );

    userRepository.delete(user);
  }
}