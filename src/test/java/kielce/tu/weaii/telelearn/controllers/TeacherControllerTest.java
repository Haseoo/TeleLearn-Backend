package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.users.EmailNotAvailableException;
import kielce.tu.weaii.telelearn.exceptions.users.UsernameNotAvailableException;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.TeacherService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    public TeacherService teacherService;

    @MockBean
    public UserService userService;

    @Test
    void should_ask_for_all_teachers_and_return_200() throws Exception {
        //given
        when(teacherService.getAll()).thenReturn(Arrays.asList(TestData.getTeacher(), TestData.getTeacher()));
        //when & then
        mockMvc.perform(get("/api/user/teacher")).andExpect(status().isOk());
        verify(teacherService).getAll();
    }

    @Test
    void should_ask_for_teacher_and_return_404_when_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(teacherService.getById(id)).thenThrow(new NotFoundException("teacher"));
        //when & then
        mockMvc.perform(get("/api/user/teacher/" + id)).andExpect(status().isNotFound());
        verify(teacherService).getById(id);
    }

    @Test
    void should_ask_for_teacher_and_return_200_with_username_when_current_user_is_subject_or_admin() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        when(teacherService.getById(id)).thenReturn(teacher);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/teacher/" + id)).andReturn();
        //then
        verify(teacherService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains(String.format("\"username\":\"%s\"", teacher.getUsername()));
    }

    @Test
    void should_ask_for_teacher_and_return_200_with_username_when_current_user_is_neither_subject_nor_admin() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        when(teacherService.getById(id)).thenReturn(teacher);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(false);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/teacher/" + id)).andReturn();
        //then
        verify(teacherService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("\"username\":null");
    }

    @Test
    void should_ask_to_update_teacher_and_return_403_when_current_user_has_not_permission() throws Exception {
        //given
        final Long id = 1L;
        TeacherUpdateRequest request = TestData.getTeacherUpdateRequest();
        when(teacherService.update(any(), any())).thenThrow(new AuthorizationException("update teacher", null, id));
        //when & then
        mockMvc.perform(put("/api/user/teacher/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        verify(teacherService).update(eq(id), eq(request));
    }

    @Test
    void should_ask_to_update_teacher_and_return_400_when_email_is_taken() throws Exception {
        //given
        final Long id = 1L;
        TeacherUpdateRequest request = TestData.getTeacherUpdateRequest();
        when(teacherService.update(any(), any())).thenThrow(new EmailNotAvailableException(request.getEmail()));
        //when & then
        mockMvc.perform(put("/api/user/teacher/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(teacherService).update(eq(id), eq(request));
    }

    @Test
    void should_ask_to_update_teacher_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        TeacherUpdateRequest request = TestData.getTeacherUpdateRequest();
        when(teacherService.update(any(), any())).thenReturn(TestData.getTeacher());
        //when & then
        mockMvc.perform(put("/api/user/teacher/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(teacherService).update(eq(id), eq(request));
    }

    @Test
    void should_ask_to_register_teacher_and_return_400_when_username_is_taken() throws Exception {
        //given
        TeacherRegisterRequest request = TestData.getTeacherRegisterRequest();
        when(teacherService.add(any())).thenThrow(new UsernameNotAvailableException(request.getUsername()));
        //when & then
        mockMvc.perform(post("/api/user/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(teacherService).add(eq(request));
    }

    @Test
    void should_ask_to_register_teacher_and_return_400_when_email_is_taken() throws Exception {
        //given
        TeacherRegisterRequest request = TestData.getTeacherRegisterRequest();
        when(teacherService.add(any())).thenThrow(new EmailNotAvailableException(request.getEmail()));
        //when & then
        mockMvc.perform(post("/api/user/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(teacherService).add(eq(request));
    }

    @Test
    void should_ask_to_register_teacher_and_return_201() throws Exception {
        //given
        TeacherRegisterRequest request = TestData.getTeacherRegisterRequest();
        Teacher teacher = TestData.getTeacher();
        when(teacherService.add(any())).thenReturn(teacher);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/user/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        //then
        verify(teacherService).add(eq(request));
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        Assertions.assertThat(mvcResult.getResponse().getHeader("Location"))
                .contains(teacher.getId().toString());
    }

    @Test
    void should_ask_to_delete_teacher_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/user/teacher/" + id)).andExpect(status().isNoContent());
        verify(teacherService).delete(id);
    }

    @Test
    void should_ask_for_teacher_and_return_200_and_theirs_course_briefs() throws Exception {
        //given
        Teacher teacher = TestData.getTeacher();
        final Long id = teacher.getId();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        teacher.getCourses().add(course);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(false);
        when(teacherService.getCourses(id)).thenReturn(Arrays.asList(course));
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/teacher/" + id + "/courses")).andReturn();
        //then
        verify(teacherService).getCourses(id);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .doesNotContain("areStudentsAllowedToPost")
                .doesNotContain("welcomePageHtmlContent")
                .doesNotContain("requestedStudents")
                .doesNotContain("isPublicCourse");
    }

    @Test
    void should_ask_for_teacher_and_return_200_and_theirs_course_full_view() throws Exception {
        //given
        Teacher teacher = TestData.getTeacher();
        final Long id = teacher.getId();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        teacher.getCourses().add(course);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        when(teacherService.getCourses(id)).thenReturn(Arrays.asList(course));
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/teacher/" + id + "/courses")).andReturn();
        //then
        verify(teacherService).getCourses(id);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("areStudentsAllowedToPost")
                .contains("welcomePageHtmlContent")
                .contains("requestedStudents")
                .contains("isPublicCourse");
    }
}