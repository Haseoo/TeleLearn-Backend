package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.PreviousTaskNotCompleted;
import kielce.tu.weaii.telelearn.exceptions.courses.SchedulePlannedTimeUpdateNotPossible;
import kielce.tu.weaii.telelearn.exceptions.courses.UpdateLearningTimeNotPossible;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.requests.TimeSpanRequest;
import kielce.tu.weaii.telelearn.requests.courses.RecordLearningRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.TaskScheduleService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "student", password = "student", roles = "STUDENT")
@Tag(INTEGRATION_TEST)
class TaskScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private TaskScheduleService taskScheduleService;

    @Test
    void should_ask_for_schedule_by_id_and_return_403_when_user_has_not_permission() throws Exception {
        //given
        final Long id = 1L;
        when(taskScheduleService.getById(any())).thenThrow(new AuthorizationException("schedule", null, id));
        //when
        mockMvc.perform(get("/api/schedule/" + id)).andExpect(status().isForbidden());
        verify(taskScheduleService).getById(id);
    }

    @Test
    void should_ask_for_schedule_by_id_and_return_404_when_schedule_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(taskScheduleService.getById(any())).thenThrow(new NotFoundException("schedule"));
        //when
        mockMvc.perform(get("/api/schedule/" + id)).andExpect(status().isNotFound());
        verify(taskScheduleService).getById(id);
    }

    @Test
    void should_ask_for_schedule_by_id_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        Student student = TestData.getStudent();
        Course course = TestData.getCourse(TestData.getTeacher(), student);
        Task task = TestData.getTask(course);
        when(taskScheduleService.getById(any())).thenReturn(TestData.getTaskScheduleRecord(task, student));
        //when
        mockMvc.perform(get("/api/schedule/" + id)).andExpect(status().isOk());
        verify(taskScheduleService).getById(id);
    }

    @Test
    void should_ask_for_update_schedule_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(new TimeSpanRequest(1, 12), "11:12");
        //when & then
        mockMvc.perform(put("/api/schedule/" + id + "/planned-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\":{\"hours\":1,\"minutes\":12},\"startTime\":\"11:12\"}"))
                .andExpect(status().isNoContent());
        verify(taskScheduleService).updateSchedule(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_update_schedule_and_return_403() throws Exception {
        //given
        final Long id = 1L;
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(new TimeSpanRequest(1, 12), "11:12");
        when(taskScheduleService.updateSchedule(any(), any(), any())).thenThrow(new AuthorizationException("schedule", null, id));
        //when & then
        mockMvc.perform(put("/api/schedule/" + id + "/planned-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\":{\"hours\":1,\"minutes\":12},\"startTime\":\"11:12\"}"))
                .andExpect(status().isForbidden());
        verify(taskScheduleService).updateSchedule(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_update_schedule_and_return_400() throws Exception {
        //given
        final Long id = 1L;
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(new TimeSpanRequest(1, 12), "11:12");
        when(taskScheduleService.updateSchedule(any(), any(), any())).thenThrow(new SchedulePlannedTimeUpdateNotPossible());
        //when & then
        mockMvc.perform(put("/api/schedule/" + id + "/planned-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\":{\"hours\":1,\"minutes\":12},\"startTime\":\"11:12\"}"))
                .andExpect(status().isBadRequest());
        verify(taskScheduleService).updateSchedule(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_update_learning_time_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 12));
        //when & then
        mockMvc.perform(patch("/api/schedule/" + id + "/learning-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"startTime\":\"11:12\",\"duration\":{\"hours\":1,\"minutes\":12}}"))
                .andExpect(status().isNoContent());
        verify(taskScheduleService).updateLearningTime(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_update_learning_time_and_return_400_when_recording_in_other_day() throws Exception {
        //given
        final Long id = 1L;
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 12));
        when(taskScheduleService.updateLearningTime(any(), any(), any())).thenThrow(new UpdateLearningTimeNotPossible());
        //when & then
        mockMvc.perform(patch("/api/schedule/" + id + "/learning-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"startTime\":\"11:12\",\"duration\":{\"hours\":1,\"minutes\":12}}"))
                .andExpect(status().isBadRequest());
        verify(taskScheduleService).updateLearningTime(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_update_learning_time_and_return_400_when_previous_task_is_not_complete() throws Exception {
        //given
        final Long id = 1L;
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 12));
        when(taskScheduleService.updateLearningTime(any(), any(), any())).thenThrow(new PreviousTaskNotCompleted());
        //when & then
        mockMvc.perform(patch("/api/schedule/" + id + "/learning-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"startTime\":\"11:12\",\"duration\":{\"hours\":1,\"minutes\":12}}"))
                .andExpect(status().isBadRequest());
        verify(taskScheduleService).updateLearningTime(eq(id), eq(request), any());
    }

    @Test
    void should_ask_for_delete_course_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/schedule/" + id)).andExpect(status().isNoContent());
        verify(taskScheduleService).delete(id);
    }

}