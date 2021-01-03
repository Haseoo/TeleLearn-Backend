package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.users.EmailNotAvailableException;
import kielce.tu.weaii.telelearn.exceptions.users.UsernameNotAvailableException;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.*;
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

import java.time.LocalDate;
import java.util.Arrays;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "student", password = "student", roles = "STUDENT")
@Tag(INTEGRATION_TEST)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    public StudentService studentService;

    @MockBean
    public TaskService taskService;

    @MockBean
    public TaskScheduleService taskScheduleService;

    @MockBean
    private StudentStatsService studentStatsService;

    @MockBean
    public UserService userService;

    @Test
    void should_ask_for_student_and_return_404_when_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(studentService.getById(id)).thenThrow(new NotFoundException("student"));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id)).andExpect(status().isNotFound());
        verify(studentService).getById(id);
    }

    @Test
    void should_ask_for_student_and_return_200_with_username_when_current_user_is_subject_or_admin() throws Exception {
        //given
        final Long id = 1L;
        Student student = TestData.getStudent();
        when(studentService.getById(id)).thenReturn(student);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/student/" + id)).andReturn();
        //then
        verify(studentService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains(String.format("\"username\":\"%s\"", student.getUsername()));
    }

    @Test
    void should_ask_for_student_and_return_200_with_username_when_current_user_is_neither_subject_nor_admin() throws Exception {
        //given
        final Long id = 1L;
        Student student = TestData.getStudent();
        when(studentService.getById(id)).thenReturn(student);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(false);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/user/student/" + id)).andReturn();
        //then
        verify(studentService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("\"username\":null");
    }

    @Test
    void should_ask_for_student_stats_and_return_403_when_user_has_not_permission() throws Exception {
        //given
        final Long id = 1L;
        final LocalDate today = LocalDate.now();
        when(studentStatsService.getStudentStat(any(), any())).thenThrow(new AuthorizationException("student stats", null, id));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/stat")).andExpect(status().isForbidden());
        verify(studentStatsService).getStudentStat(id, today);
    }

    @Test
    void should_ask_for_student_stats_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        final LocalDate today = LocalDate.now();
        when(studentStatsService.getStudentStat(any(), any())).thenReturn(TestData.getStudentStats());
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/stat")).andExpect(status().isOk());
        verify(studentStatsService).getStudentStat(id, today);
    }

    @Test
    void should_ask_to_update_student_and_return_400_when_email_is_taken() throws Exception {
        //given
        final Long id = 1L;
        StudentUpdateRequest request = TestData.getStudentUpdateRequest();
        when(studentService.update(any(), any())).thenThrow(new EmailNotAvailableException(request.getEmail()));
        //when & then
        mockMvc.perform(put("/api/user/student/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(studentService).update(eq(id), eq(request));
    }

    @Test
    void should_ask_to_update_student_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        StudentUpdateRequest request = TestData.getStudentUpdateRequest();
        when(studentService.update(any(), any())).thenReturn(TestData.getStudent());
        //when & then
        mockMvc.perform(put("/api/user/student/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(studentService).update(eq(id), eq(request));
    }

    @Test
    void should_ask_to_register_student_and_return_400_when_username_is_taken() throws Exception {
        //given
        StudentRegisterRequest request = TestData.getStudentRegisterRequest();
        when(studentService.add(any())).thenThrow(new UsernameNotAvailableException(request.getUsername()));
        //when & then
        mockMvc.perform(post("/api/user/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(studentService).add(eq(request));
    }

    @Test
    void should_ask_to_register_student_and_return_400_when_email_is_taken() throws Exception {
        //given
        StudentRegisterRequest request = TestData.getStudentRegisterRequest();
        when(studentService.add(any())).thenThrow(new EmailNotAvailableException(request.getEmail()));
        //when & then
        mockMvc.perform(post("/api/user/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(studentService).add(eq(request));
    }

    @Test
    void should_ask_to_register_teacher_and_return_201() throws Exception {
        //given
        StudentRegisterRequest request = TestData.getStudentRegisterRequest();
        Student student = TestData.getStudent();
        when(studentService.add(any())).thenReturn(student);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/user/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        //then
        verify(studentService).add(eq(request));
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        Assertions.assertThat(mvcResult.getResponse().getHeader("Location"))
                .contains(student.getId().toString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
    void should_ask_to_delete_student_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/user/student/" + id)).andExpect(status().isNoContent());
        verify(studentService).delete(id);
    }

    @Test
    void should_ask_for_student_courses_and_return_403_when_user_has_not_permission() throws Exception {
        //given
        final Long id = 1L;
        when(studentService.getCourses(id)).thenThrow(new AuthorizationException("student courses", null, id));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/courses")).andExpect(status().isForbidden());
        verify(studentService).getCourses(id);
    }

    @Test
    void should_ask_for_student_courses_and_return_200() throws Exception {
        //given
        Student student = TestData.getStudent();
        final Long id = 1L;
        when(studentService.getCourses(id))
                .thenReturn(Arrays.asList(TestData.getCourse(TestData.getTeacher(), student),
                        TestData.getCourse(TestData.getTeacher(), student)));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/courses")).andExpect(status().isOk());
        verify(studentService).getCourses(id);
    }

    @Test
    void should_ask_for_student_tasks_and_return_403_when_user_has_not_permission() throws Exception {
        //given
        LocalDate today = LocalDate.now();
        final Long id = 1L;
        when(taskService.getStudentByTasksFromCurse(id, today)).thenThrow(new AuthorizationException("student tasks", null, id));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/tasks")).andExpect(status().isForbidden());
        verify(taskService).getStudentByTasksFromCurse(id, today);
    }

    @Test
    void should_ask_for_student_tasks_and_return_200() throws Exception {
        //given
        LocalDate today = LocalDate.now();
        final Long id = 1L;
        when(taskService.getStudentByTasksFromCurse(id, today)).thenReturn(TestData.getTaskStudentSummary(today));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/tasks")).andExpect(status().isOk());
        verify(taskService).getStudentByTasksFromCurse(id, today);
    }

    @Test
    void should_ask_for_student_schedule_and_return_403_when_user_has_not_permission() throws Exception {
        //given
        final Long id = 1L;
        when(taskScheduleService.getListForStudent(id)).thenThrow(new AuthorizationException("student schedule", null, id));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/schedule")).andExpect(status().isForbidden());
        verify(taskScheduleService).getListForStudent(id);
    }

    @Test
    void should_ask_for_student_schedule_and_return_200() throws Exception {
        //given
        LocalDate today = LocalDate.now();
        final Long id = 1L;
        when(taskScheduleService.getListForStudent(id)).thenReturn(Arrays.asList(TestData.getTaskScheduleRecord(today)));
        //when & then
        mockMvc.perform(get("/api/user/student/" + id + "/schedule")).andExpect(status().isOk());
        verify(taskScheduleService).getListForStudent(id);
    }
}