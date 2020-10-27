package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.users.EmailNotAvailableException;
import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.exceptions.users.UsernameNotAvailableException;
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
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public void checkAvailability(String login, String email) {
        if (userRepository.getUserByLogin(login).isPresent()) {
            throw new UsernameNotAvailableException(login);
        }
        if (userRepository.getUserByEmail(email).isPresent()) {
            throw new EmailNotAvailableException(email);
        }
    }

    public JwtAuthenticationResponse getJwt(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        String.valueOf(loginRequest.getPassword())
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }

    public User getUserByLoginOrEmail(String loginOrEmail) {
        return userRepository.getUserByLoginOrEmail(loginOrEmail).orElseThrow(() -> new UserNotFoundException(loginOrEmail));
    }
}
