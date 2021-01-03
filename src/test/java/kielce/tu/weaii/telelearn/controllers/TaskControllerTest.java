package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.TaskNotFound;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.requests.courses.TaskProgressPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRepeatPatchRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.TaskScheduleService;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
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
@WithMockUser(username = "teacher", password = "teacher", roles = "TEACHER")
@Tag(INTEGRATION_TEST)
class TaskControllerTest {

    @MockBean
    private TaskService taskService;
    @MockBean
    private UserServiceDetailsImpl userServiceDetails;
    @MockBean
    private TaskScheduleService taskScheduleService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_ask_for_task_by_id_and_return_without_student_info() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        Student student = TestData.getStudent();
        Task task = TestData.getTask(TestData.getCourse(teacher, student));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        when(taskService.getById(anyLong())).thenReturn(task);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/task/" + id)).andReturn();
        //then
        verify(taskService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .doesNotContain("taskCompletion")
                .doesNotContain("isLearnable")
                .doesNotContain("isToRepeat");
    }

    @Test
    void should_ask_for_task_by_id_and_return_with_student_info() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        Student student = TestData.getStudent();
        Task task = TestData.getTask(TestData.getCourse(teacher, student));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(taskService.getById(anyLong())).thenReturn(task);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/task/" + id)).andReturn();
        //then
        verify(taskService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("taskCompletion")
                .contains("isLearnable")
                .contains("isToRepeat");
    }

    @Test
    void should_ask_for_task_by_id_and_return_404_when_task_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(taskService.getById(anyLong())).thenThrow(new TaskNotFound(id));
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when & then
        mockMvc.perform(get("/api/task/" + id)).andExpect(status().isNotFound());
        verify(taskService).getById(id);
    }

    @Test
    void should_ask_for_task_by_id_and_return_403() throws Exception {
        //given
        final Long id = 1L;
        when(taskService.getById(anyLong())).thenThrow(new AuthorizationException("task", null, id));
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when & then
        mockMvc.perform(get("/api/task/" + id)).andExpect(status().isForbidden());
        verify(taskService).getById(id);
    }

    @Test
    @WithMockUser(username = "s", password = "s", roles = "STUDENT")
    void should_ask_for_task_student_schedule_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        Student student = TestData.getStudent();
        when(taskScheduleService.getListForTaskAndStudent(any(), any()))
                .thenReturn(Arrays.asList(TestData.getTaskScheduleRecord(LocalDate.now())));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        mockMvc.perform(get("/api/task/" + id + "/schedule")).andExpect(status().isOk());
        verify(taskScheduleService).getListForTaskAndStudent(student.getId(), id);
    }

    @Test
    void should_ask_to_remove_task_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/task/" + id)).andExpect(status().isNoContent());
        verify(taskService).delete(id);
    }

    @Test
    @WithMockUser(username = "s", password = "s", roles = "STUDENT")
    void should_ask_to_set_progress_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        TaskProgressPatchRequest request = new TaskProgressPatchRequest(2L, 50);
        //when & then
        mockMvc.perform(patch("/api/task/" + id + "/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(taskService).updateProgress(eq(id), eq(request));
    }

    @Test
    @WithMockUser(username = "s", password = "s", roles = "STUDENT")
    void should_ask_to_set_to_repeat_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        TaskRepeatPatchRequest request = new TaskRepeatPatchRequest(2L, true);
        //when & then
        mockMvc.perform(patch("/api/task/" + id + "/repeat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(taskService).updateTaskRepeat(eq(id), eq(request));
    }
}