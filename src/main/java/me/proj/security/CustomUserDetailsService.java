package me.proj.security;

import me.proj.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByName(username)
                .filter(user -> user.getPasswordHash() != null)
                .map(SecurityUser::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }
}
