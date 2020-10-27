package kielce.tu.weaii.telelearn.security;

import kielce.tu.weaii.telelearn.exceptions.UserNotFoundException;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.ports.UserRepository;
import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.security.JwtAuthenticationResponse;
import kielce.tu.weaii.telelearn.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
