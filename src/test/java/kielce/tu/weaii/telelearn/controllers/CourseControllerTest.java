package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.CourseNotFoundException;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.requests.courses.CourseStudentRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.PostService;
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
import java.util.List;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "teacher", password = "teacher", roles = "TEACHER")
@Tag(INTEGRATION_TEST)
class CourseControllerTest {

    @MockBean
    private CourseService courseService;
    @MockBean
    private PostService postService;
    @MockBean
    private UserServiceDetailsImpl userServiceDetails;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_ask_for_course_by_id_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        when(courseService.getById(anyLong()))
                .thenReturn(TestData.getCourse(TestData.getTeacher(), TestData.getStudent()));
        //when & then
        mockMvc.perform(get("/api/course/" + id)).andExpect(status().isOk());
        verify(courseService).getById(id);
    }

    @Test
    void should_ask_for_course_by_id_and_return_404_when_course_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(courseService.getById(anyLong())).thenThrow(new CourseNotFoundException(id));
        //when & then
        mockMvc.perform(get("/api/course/" + id)).andExpect(status().isNotFound());
        verify(courseService).getById(id);
    }

    @Test
    void should_ask_for_course_by_id_and_return_403() throws Exception {
        //given
        final Long id = 1L;
        when(courseService.getById(anyLong())).thenThrow(new AuthorizationException("course", null, id));
        //when & then
        mockMvc.perform(get("/api/course/" + id)).andExpect(status().isForbidden());
        verify(courseService).getById(id);
    }

    @Test
    void should_ask_to_delete_course_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/course/" + id)).andExpect(status().isNoContent());
        verify(courseService).delete(id);
    }

    @Test
    void should_ask_to_sign_up_student_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        CourseStudentRequest request = new CourseStudentRequest(5L);
        //when
        mockMvc.perform(put("/api/course/" + id + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(courseService).signUpStudent(eq(id), eq(request.getStudentId()));
    }

    @Test
    void should_accept_student_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        CourseStudentRequest request = new CourseStudentRequest(5L);
        //when
        mockMvc.perform(put("/api/course/" + id + "/accept-student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(courseService).acceptStudent(eq(id), eq(request.getStudentId()));
    }

    @Test
    void should_sign_out_student_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        CourseStudentRequest request = new CourseStudentRequest(5L);
        //when
        mockMvc.perform(put("/api/course/" + id + "/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(courseService).signOutStudent(eq(id), eq(request.getStudentId()));
    }

    @Test
    void should_ask_for_course_by_id_and_return_200_and_brief() throws Exception {
        //given
        final Long id = 1L;
        when(courseService.getCourse(any()))
                .thenReturn(TestData.getCourse(TestData.getTeacher(), TestData.getStudent()));
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/course/" + id + "/brief")).andReturn();
        //then
        verify(courseService).getCourse(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .doesNotContain("areStudentsAllowedToPost")
                .doesNotContain("welcomePageHtmlContent")
                .doesNotContain("requestedStudents")
                .doesNotContain("isPublicCourse");
    }

    @Test
    void should_ask_for_course_post_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        List<Post> posts = Arrays.asList(TestData.getPost(course, TestData.getAdmin()));
        when(postService.getCoursePosts(anyLong())).thenReturn(posts);
        //when & then
        mockMvc.perform(get("/api/course/" + id + "/post")).andExpect(status().isOk());
        verify(postService).getCoursePosts(id);
    }

    @Test
    void should_ask_for_course_task_and_return_200_for_teacher() throws Exception {
        //given
        final Long id = 1L;
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        List<Task> tasks = Arrays.asList(TestData.getTask(course));
        course.setTasks(tasks);
        when(courseService.getById(anyLong())).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getTeacher());
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/course/" + id + "/task")).andReturn();
        //then
        verify(courseService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .doesNotContain("taskCompletion")
                .doesNotContain("isLearnable")
                .doesNotContain("isToRepeat");
    }

    @Test
    void should_ask_for_course_task_and_return_200_for_student() throws Exception {
        //given
        final Long id = 1L;
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        List<Task> tasks = Arrays.asList(TestData.getTask(course));
        course.setTasks(tasks);
        when(courseService.getById(anyLong())).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/course/" + id + "/task")).andReturn();
        //then
        verify(courseService).getById(id);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(mvcResult.getResponse().getContentAsString())
                .contains("taskCompletion")
                .contains("isLearnable")
                .contains("isToRepeat");
    }
}