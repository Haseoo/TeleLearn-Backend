package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.CommentNotFound;
import kielce.tu.weaii.telelearn.services.ports.PostService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class CommentControllerTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_ask_for_delete_comment_and_return_204() throws Exception {
        //given
        final Long id = 1L;
        //when & then
        mockMvc.perform(delete("/api/comment/" + id))
                .andExpect(status().isNoContent());
        verify(postService).deleteComment(id);
    }

    @Test
    void should_ask_for_delete_comment_and_return_403_when_is_not_user_comment() throws Exception {
        //given
        final Long id = 1L;
        doThrow(new AuthorizationException("comment", null, id)).when(postService).deleteComment(id);
        //when & then
        mockMvc.perform(delete("/api/comment/" + id))
                .andExpect(status().isForbidden());
        verify(postService).deleteComment(id);
    }

    @Test
    void should_ask_for_delete_comment_and_return_404_comment_doesnt_exist() throws Exception {
        //given
        final Long id = 1L;
        doThrow(new CommentNotFound(id)).when(postService).deleteComment(id);
        //when & then
        mockMvc.perform(delete("/api/comment/" + id))
                .andExpect(status().isNotFound());
        verify(postService).deleteComment(id);
    }
}