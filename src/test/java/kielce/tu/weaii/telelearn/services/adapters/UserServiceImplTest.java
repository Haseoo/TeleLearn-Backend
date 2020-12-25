package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.users.EmailNotAvailableException;
import kielce.tu.weaii.telelearn.exceptions.users.InvalidPasswordException;
import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.exceptions.users.UsernameNotAvailableException;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.ports.UserRepository;
import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.requests.UserPasswordPatchRequest;
import kielce.tu.weaii.telelearn.security.JwtAuthenticationResponse;
import kielce.tu.weaii.telelearn.security.JwtTokenProvider;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserServiceImpl sut;

    @Test
    void should_throw_email_not_available_exception_when_email_is_taken_on_check_email() {
        //given
        final String email = "test@test.com";
        when(userRepository.getUserByEmail(email)).thenReturn(java.util.Optional.of(TestData.getAdmin()));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.checkAvailability(email)).isInstanceOf(EmailNotAvailableException.class);
        verify(userRepository).getUserByEmail(email);
    }

    @Test
    void should_not_throw_email_not_available_exception_on_check_email() {
        //given
        final String email = "test@test.com";
        when(userRepository.getUserByEmail(email)).thenReturn(java.util.Optional.empty());
        //when
        sut.checkAvailability(email);
        //then
        verify(userRepository).getUserByEmail(email);
    }

    @Test
    void should_not_throw_email_not_available_exception_on_check_email_or_login() {
        //given
        final String login = "login";
        final String email = "test@test.com";
        when(userRepository.getUserByLogin(login)).thenReturn(Optional.empty());
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());
        //when & then
        sut.checkAvailability(login, email);
        verify(userRepository).getUserByEmail(email);
        verify(userRepository).getUserByLogin(login);
    }

    @Test
    void should_throw_email_not_available_exception_when_email_is_taken_on_check_email_or_login() {
        //given
        final String login = "login";
        final String email = "test@test.com";
        when(userRepository.getUserByLogin(login)).thenReturn(Optional.empty());
        when(userRepository.getUserByEmail(email)).thenReturn(java.util.Optional.of(TestData.getAdmin()));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.checkAvailability(login, email)).isInstanceOf(EmailNotAvailableException.class);
        verify(userRepository).getUserByEmail(email);
        verify(userRepository).getUserByLogin(login);
    }

    @Test
    void should_not_throw_username_not_available_exception_on_check_email_or_login() {
        //given
        final String login = "login";
        when(userRepository.getUserByLogin(login)).thenReturn(java.util.Optional.empty());
        //when & then
        sut.checkAvailability(login, "");
        verify(userRepository).getUserByLogin(login);
    }

    @Test
    void should_throw_username_not_available_exception_when_login_is_taken_on_check_email_or_login() {
        //given
        final String login = "login";
        when(userRepository.getUserByLogin(login)).thenReturn(java.util.Optional.of(TestData.getAdmin()));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.checkAvailability(login, "")).isInstanceOf(UsernameNotAvailableException.class);
        verify(userRepository).getUserByLogin(login);
    }

    @Test
    void should_return_jwt_token() {
        //given
        final String login = "login";
        final String secret = "secret";
        LoginRequest request = new LoginRequest(login, secret.toCharArray());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationManagerRequest = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        //when
        JwtAuthenticationResponse out = sut.getJwt(request);
        //then
        verify(authenticationManager).authenticate(authenticationManagerRequest.capture());
        Assertions.assertThat(authenticationManagerRequest.getValue().getCredentials()).isEqualTo(secret);
        Assertions.assertThat(authenticationManagerRequest.getValue().getPrincipal()).isEqualTo(login);
        Assertions.assertThat(out.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void should_ask_for_and_return_user_by_email_or_login() {
        //given
        User searchedUser = TestData.getTeacher();
        final String loginOrEmail = searchedUser.getEmail();
        when(userRepository.getUserByLoginOrEmail(loginOrEmail)).thenReturn(java.util.Optional.of(searchedUser));
        //when
        User out = sut.getUserByLoginOrEmail(loginOrEmail);
        //then
        verify(userRepository).getUserByLoginOrEmail(loginOrEmail);
        Assertions.assertThat(out).isEqualTo(searchedUser);
    }

    @Test
    void should_throw_user_not_found_exception_when_user_with_email_or_login_doesnt_exist() {
        //given
        final String loginOrEmail = "IDontExist";
        when(userRepository.getUserByLoginOrEmail(loginOrEmail)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getUserByLoginOrEmail(loginOrEmail)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void should_ask_for_all_users_and_return_list() {
        //given
        List<User> mock = Arrays.asList(TestData.getAdmin(), TestData.getTeacher());
        when(userRepository.getAll()).thenReturn(mock);
        //when
        List<User> out = sut.getList();
        //then
        verify(userRepository, atMostOnce()).getAll();
        Assertions.assertThat(out).isEqualTo(mock);
    }

    @Test
    void should_ask_for_and_return_user_by_id() {
        //given
        User searchedUser = TestData.getTeacher();
        final Long id = searchedUser.getId();
        when(userRepository.getById(id)).thenReturn(java.util.Optional.of(searchedUser));
        //when
        User out = sut.getById(id);
        //then
        verify(userRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(searchedUser);
    }

    @Test
    void should_throw_user_not_found_exception_when_user_with_id_doesnt_exist() {
        //given
        final Long id = 0L;
        when(userRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(UserNotFoundException.class);
    }
    @Test
    void should_save_and_return_user_with_changed_password() {
        //given
        User user = TestData.getStudent();
        final Long id = user.getId();
        final char[] newSecret = "new".toCharArray();
        when(userRepository.getById(id)).thenReturn(java.util.Optional.of(user));
        final UserPasswordPatchRequest request = new UserPasswordPatchRequest(user.getPassword().toCharArray(), newSecret);
        when(passwordEncoder.matches(String.valueOf(request.getOldPassword()), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(String.valueOf(request.getNewPassword()))).thenReturn(user.getPassword());
        //when
        sut.updatePassword(id, request);
        //then
        verify(userRepository).save(user);
    }

    @Test
    void should_save_throw_invalid_password_exception_when_passwords_doesnt_match() {
        //given
        User user = TestData.getStudent();
        final Long id = user.getId();
        final char[] newSecret = "new".toCharArray();
        when(userRepository.getById(id)).thenReturn(java.util.Optional.of(user));
        final UserPasswordPatchRequest request = new UserPasswordPatchRequest(user.getPassword().toCharArray(), newSecret);
        when(passwordEncoder.matches(String.valueOf(request.getOldPassword()), user.getPassword())).thenReturn(false);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updatePassword(id, request)).isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void should_ask_for_delete_user() {
        //given
        User userToDelete = TestData.getTeacher();
        final Long id = userToDelete.getId();
        when(userRepository.getById(id)).thenReturn(java.util.Optional.of(userToDelete));
        //when
        sut.delete(id);
        //then
        verify(userRepository).delete(userToDelete);
    }

    @Test
    void should_return_true_when_current_user_is_admin() {
        //given
        User user = TestData.getAdmin();
        final Long id = user.getId();
        when(userServiceDetails.getCurrentUser()).thenReturn(user);
        //when & then
        Assertions.assertThat(sut.isCurrentUserOrAdmin(id)).isTrue();
    }

    @Test
    void should_return_true_when_current_user_is_subject() {
        //given
        User user = TestData.getTeacher();
        final Long id = user.getId();
        when(userServiceDetails.getCurrentUser()).thenReturn(user);
        //when & then
        Assertions.assertThat(sut.isCurrentUserOrAdmin(id)).isTrue();
    }

    @Test
    void should_return_false_when_current_user_is_not_subject() {
        //given
        User user = TestData.getTeacher();
        final Long id = 0L;
        when(userServiceDetails.getCurrentUser()).thenReturn(user);
        //when & then
        Assertions.assertThat(sut.isCurrentUserOrAdmin(id)).isFalse();
    }

    @Test
    void should_throw_authorization_exception_when_user_is_neither_admin_or_subject() {
        //given
        User user = TestData.getTeacher();
        final Long id = 0L;
        when(userServiceDetails.getCurrentUser()).thenReturn(user);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.verifyPermissionToUser(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_not_throw_authorization_exception_when_user_is_admin_or_subject() {
        //given
        User user = TestData.getTeacher();
        final Long id = user.getId();
        when(userServiceDetails.getCurrentUser()).thenReturn(user);
        //when & then
        Assertions.assertThatCode(() -> sut.verifyPermissionToUser(id)).doesNotThrowAnyException();
    }
}