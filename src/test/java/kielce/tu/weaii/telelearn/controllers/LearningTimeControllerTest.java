package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.requests.LearningTimeRequest;
import kielce.tu.weaii.telelearn.services.ports.LearningTimeService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "student", password = "student1", roles = "STUDENT")
@Tag(INTEGRATION_TEST)
class LearningTimeControllerTest {

    @MockBean
    private LearningTimeService learningTimeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_ask_to_set_learning_time_and_return_403_when_is_not_current_user() throws Exception {
        //given
        LearningTimeRequest request = TestData.getLearningTimeRequest(15L, LocalDate.of(2021, 1, 2));
        String jsonIn = "{\"studentId\":15,\"date\":\"02.01.2021\",\"time\":{\"hours\":0,\"minutes\":0}}";
        when(learningTimeService.setLearningTime(any()))
                .thenThrow(new AuthorizationException("learning time", null, request.getStudentId()));
        //when & then
        mockMvc.perform(put("/api/learning-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
                .andExpect(status().isForbidden());
        verify(learningTimeService).setLearningTime(eq(request));
    }

    @Test
    void should_ask_to_set_learning_time_and_return_204() throws Exception {
        //given
        LearningTimeRequest request = TestData.getLearningTimeRequest(15L, LocalDate.of(2021, 1, 2));
        String jsonIn = "{\"studentId\":15,\"date\":\"02.01.2021\",\"time\":{\"hours\":0,\"minutes\":0}}";
        //when & then
        mockMvc.perform(put("/api/learning-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
                .andExpect(status().isNoContent());
        verify(learningTimeService).setLearningTime(eq(request));
    }

    @Test
    void should_ask_for_student_schedule_and_return_403_when_is_not_current_user() throws Exception {
        //given
        final Long id = 1L;
        when(learningTimeService.getForStudent(id)).thenThrow(new AuthorizationException("plan", null, id));
        //when
        mockMvc.perform(get("/api/learning-time/" + id))
                .andExpect(status().isForbidden());
        verify(learningTimeService).getForStudent(id);
    }

    @Test
    void should_ask_for_student_schedule_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        when(learningTimeService.getForStudent(id)).thenReturn(TestData.getLearningTimeData());
        //when
        mockMvc.perform(get("/api/learning-time/" + id))
                .andExpect(status().isOk());
        verify(learningTimeService).getForStudent(id);
    }
}