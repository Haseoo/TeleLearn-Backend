package kielce.tu.weaii.telelearn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.InvalidSenderException;
import kielce.tu.weaii.telelearn.models.Message;
import kielce.tu.weaii.telelearn.requests.SendMessageRequest;
import kielce.tu.weaii.telelearn.servicedata.ConversationInfo;
import kielce.tu.weaii.telelearn.services.ports.MessageService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin1", roles = "ADMIN")
@Tag(INTEGRATION_TEST)
class MessageControllerTest {

    @MockBean
    private MessageService messageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void should_ask_for_conversations_and_return_403_when_user_is_not_participant() throws Exception {
        //given
        final Long id = 1L;
        when(messageService.getConversations(id)).thenThrow(new AuthorizationException("conversations", null, id));
        //when & then
        mockMvc.perform(get("/api/message/" + id)).andExpect(status().isForbidden());
        verify(messageService).getConversations(id);
    }

    @Test
    void should_ask_for_user_conversations_and_return_200() throws Exception {
        //given
        final Long id = 1L;
        ConversationInfo ci1 = TestData.getConversationInfo(TestData.getStudent());
        ConversationInfo ci2 = TestData.getConversationInfo(TestData.getTeacher());
        when(messageService.getConversations(id)).thenReturn(Arrays.asList(ci1, ci2));
        //when
        mockMvc.perform(get("/api/message/" + id)).andExpect(status().isOk());
        verify(messageService).getConversations(id);
    }

    @Test
    void should_ask_for_conversation_and_return_403_when_user_is_not_participant() throws Exception {
        //given
        final Long id1 = 1L;
        final Long id2 = 1L;
        when(messageService.getConversation(id1, id2)).thenThrow(new AuthorizationException("conversations", null, null));
        //when & then
        mockMvc.perform(get("/api/message/" + id1 + "/" + id2)).andExpect(status().isForbidden());
        verify(messageService).getConversation(id1, id2);
    }

    @Test
    void should_ask_for_user_conversation_and_return_200() throws Exception {
        //given
        final Long id1 = 1L;
        final Long id2 = 2L;
        Message m1 = TestData.getMessage(TestData.getTeacher(), TestData.getAdmin());
        Message m2 = TestData.getMessage(TestData.getTeacher(), TestData.getAdmin());
        when(messageService.getConversation(id1, id2)).thenReturn(Arrays.asList(m1, m2));
        //when
        mockMvc.perform(get("/api/message/" + id1 + "/" + id2)).andExpect(status().isOk());
        verify(messageService).getConversation(id1, id2);
    }

    @Test
    void should_ask_to_send_message_and_return_400_when_sender_is_invalid() throws Exception {
        //given
        final Long id1 = 1L;
        final Long id2 = 2L;
        SendMessageRequest request = new SendMessageRequest(id1, id2, "content");
        when(messageService.sendMessage(any())).thenThrow(new InvalidSenderException());
        //when & then
        mockMvc.perform(put("/api/message/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(messageService).sendMessage(eq(request));
    }

    @Test
    void should_ask_to_send_message_and_return_204() throws Exception {
        //given
        final Long id1 = 1L;
        final Long id2 = 2L;
        SendMessageRequest request = new SendMessageRequest(id1, id2, "content");
        when(messageService.sendMessage(any())).thenReturn(TestData.getMessage(TestData.getTeacher(), TestData.getAdmin()));
        //when & then
        mockMvc.perform(put("/api/message/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(messageService).sendMessage(eq(request));
    }

}