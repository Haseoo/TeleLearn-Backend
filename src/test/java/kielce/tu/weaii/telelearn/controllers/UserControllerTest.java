package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.users.InvalidPasswordException;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.requests.UserPasswordPatchRequest;
import kielce.tu.weaii.telelearn.security.JwtAuthenticationResponse;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static kielce.tu.weaii.telelearn.security.Constants.AUTH_COOKIE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_login_user_and_return_200() throws Exception {
        //given
        User mockUser = TestData.getTeacher();
        LoginRequest request = new LoginRequest(mockUser.getUsername(),
                mockUser.getPassword().toCharArray());
        when(userService.getJwt(any())).thenReturn(new JwtAuthenticationResponse("mockToken"));
        when(userService.getUserByLoginOrEmail(request.getUserName())).thenReturn(mockUser);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        //then
        verify(userService).getJwt(eq(request));
        verify(userService).getUserByLoginOrEmail(request.getUserName());
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains(mockUser.getUsername())
                .contains(mockUser.getId().toString())
                .contains(mockUser.getName())
                .contains(mockUser.getSurname())
                .contains("TEACHER");
        Assertions.assertThat(mvcResult.getResponse().getCookies())
                .anyMatch(cookie -> cookie.getName().equals(AUTH_COOKIE) &&
                        cookie.getValue().equals("mockToken"));
    }

    @Test
    void should_return_401_on_bad_credentials() throws Exception {
        //given
        User mockUser = TestData.getTeacher();
        LoginRequest request = new LoginRequest(mockUser.getUsername(),
                mockUser.getPassword().toCharArray());
        when(userService.getJwt(any())).thenThrow(new BadCredentialsException("error"));
        //when & then
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_ask_for_user_list_and_return_200() throws Exception {
        //given
        when(userService.getList()).thenReturn(Arrays.asList(TestData.getTeacher(), TestData.getAdmin(), TestData.getStudent()));
        //when & then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk());
        verify(userService).getList();
    }

    @Test
    void should_ask_for_student_id_and_return_404_when_user_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(userService.getById(id)).thenThrow(new NotFoundException("user"));
        //when & then
        mockMvc.perform(get("/api/user/" + id))
                .andExpect(status().isNotFound());
        verify(userService).getById(id);
    }

    @Test
    void should_ask_for_student_id_and_return_200_with_username_when_is_current_user_or_admin() throws Exception {
        //given
        User user = TestData.getStudent();
        final Long id = user.getId();
        when(userService.getById(id)).thenReturn(user);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/" + id))
                .andReturn();
        //then
        verify(userService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains(String.format("\"username\":\"%s\"", user.getUsername()));
    }

    @Test
    void should_ask_for_student_id_and_return_200_without_username_when_is_current_neither_user_nor_admin() throws Exception {
        //given
        User user = TestData.getStudent();
        final Long id = user.getId();
        when(userService.getById(id)).thenReturn(user);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(false);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/" + id))
                .andReturn();
        //then
        verify(userService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("\"username\":null");
    }

    @Test
    void should_ask_for_change_password_and_return_201() throws Exception {
        //given
        final Long id = 1L;
        UserPasswordPatchRequest request = new UserPasswordPatchRequest("".toCharArray(), "".toCharArray());
        //when & then
        mockMvc.perform(patch("/api/user/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(userService).updatePassword(eq(id), eq(request));
    }

    @Test
    void should_ask_for_change_password_and_return_400_when_password_is_invalid() throws Exception {
        //given
        final Long id = 1L;
        UserPasswordPatchRequest request = new UserPasswordPatchRequest("".toCharArray(), "".toCharArray());
        when(userService.updatePassword(any(), any())).thenThrow(new InvalidPasswordException());
        //when & then
        mockMvc.perform(patch("/api/user/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(userService).updatePassword(eq(id), eq(request));
    }
}