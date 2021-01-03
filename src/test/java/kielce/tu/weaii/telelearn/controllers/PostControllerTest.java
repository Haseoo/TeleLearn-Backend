package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.PostCommentingNotAllowed;
import kielce.tu.weaii.telelearn.exceptions.courses.PostNotFoundException;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.requests.courses.PostCommentRequest;
import kielce.tu.weaii.telelearn.services.ports.PostService;
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
@WithMockUser(username = "teacher", password = "teacher", roles = "TEACHER")
@Tag(INTEGRATION_TEST)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    void should_ask_for_post_by_id_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        when(postService.getById(anyLong())).thenReturn(TestData.getPost(course, teacher));
        //when & then
        mockMvc.perform(get("/api/post/" + id)).andExpect(status().isOk());
        verify(postService).getById(id);
    }

    @Test
    void should_ask_for_post_by_id_and_return_404_when_post_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        when(postService.getById(anyLong())).thenThrow(new PostNotFoundException(id));
        //when & then
        mockMvc.perform(get("/api/post/" + id)).andExpect(status().isNotFound());
        verify(postService).getById(id);
    }

    @Test
    void should_ask_for_post_by_id_and_return_403_user_is_not_in_course() throws Exception {
        //given
        final Long id = 1L;
        when(postService.getById(anyLong())).thenThrow(new AuthorizationException("post", null, id));
        //when & then
        mockMvc.perform(get("/api/post/" + id)).andExpect(status().isForbidden());
        verify(postService).getById(id);
    }

    @Test
    void should_ask_to_delete_post_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/post/" + id)).andExpect(status().isNoContent());
        verify(postService).removePost(id);
    }

    @Test
    void should_return_post_comments_and_200() throws Exception {
        //given
        final Long id = 1L;
        Teacher teacher = TestData.getTeacher();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        when(postService.getById(anyLong())).thenReturn(TestData.getPost(course, teacher));
        //when & then
        mockMvc.perform(get("/api/post/" + id + "/comment")).andExpect(status().isOk());
        verify(postService).getComments(id);
    }

    @Test
    void should_add_comment_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        PostCommentRequest request = new PostCommentRequest("content");
        //when & then
        mockMvc.perform(post("/api/post/" + id + "/comment")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(postService).addComment(eq(id), eq(request));
    }

    @Test
    void should_add_comment_and_return_400() throws Exception {
        //given
        final Long id = 1L;
        PostCommentRequest request = new PostCommentRequest("content");
        when(postService.addComment(any(), any())).thenThrow(new PostCommentingNotAllowed());
        //when & then
        mockMvc.perform(post("/api/post/" + id + "/comment")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(postService).addComment(eq(id), eq(request));
    }
}