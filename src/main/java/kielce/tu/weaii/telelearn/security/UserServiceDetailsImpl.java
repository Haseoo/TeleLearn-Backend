package kielce.tu.weaii.telelearn.security;

import kielce.tu.weaii.telelearn.exceptions.UserNotFoundException;
import kielce.tu.weaii.telelearn.repositories.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceDetailsImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        return userRepository.getUserByLoginOrEmail(usernameOrEmail).orElseThrow(() -> new UserNotFoundException(usernameOrEmail));
    }

    public UserDetails loadUserById(Long id) {
        return userRepository.getById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
