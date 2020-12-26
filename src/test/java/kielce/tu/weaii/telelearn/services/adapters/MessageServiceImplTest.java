package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.InvalidSenderException;
import kielce.tu.weaii.telelearn.models.Message;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.ports.MessageRepository;
import kielce.tu.weaii.telelearn.requests.SendMessageRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.servicedata.ConversationInfo;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserServiceDetailsImpl userDetailsService;

    @InjectMocks
    private MessageServiceImpl sut;

    @Test
    void should_throw_exception_when_current_user_is_not_subject_on_send_message() {
        //given
        User currentUserMock = TestData.getAdmin();
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        SendMessageRequest request = new SendMessageRequest(currentUserMock.getId() + 1,
                currentUserMock.getId(),
                "test");
        //when & then
        Assertions.assertThatThrownBy(() -> sut.sendMessage(request)).isInstanceOf(InvalidSenderException.class);
    }

    @Test
    void should_ask_save_message() {
        //given
        User currentUserMock = TestData.getAdmin();
        User receiver = TestData.getStudent();
        SendMessageRequest request = new SendMessageRequest(currentUserMock.getId(),
                receiver.getId(),
                "test");
        ArgumentCaptor<Message> entityToSaveRequest = ArgumentCaptor.forClass(Message.class);
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.getById(currentUserMock.getId())).thenReturn(currentUserMock);
        when(userService.getById(receiver.getId())).thenReturn(receiver);
        //when
        sut.sendMessage(request);
        //then
        verify(userService).getById(currentUserMock.getId());
        verify(userService).getById(receiver.getId());
        verify(messageRepository).save(entityToSaveRequest.capture());
        Message entityToSave = entityToSaveRequest.getValue();
        Assertions.assertThat(entityToSave.getReceiver()).isEqualTo(receiver);
        Assertions.assertThat(entityToSave.getSender()).isEqualTo(currentUserMock);
        Assertions.assertThat(entityToSave.getContent()).isEqualTo(request.getContent());
    }

    @Test
    void should_throw_exception_when_current_user_is_not_subject_on_get_conversation() {
        //given
        User currentUserMock = TestData.getAdmin();
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getConversations(currentUserMock.getId() + 1)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_user_and_return_conversation() {
        //given
        User currentUserMock = TestData.getAdmin();
        User participant1 = TestData.getTeacher();
        User participant2 = TestData.getStudent();
        Message msg1 = TestData.getMessage(currentUserMock, participant1);
        Message msg2 = TestData.getMessage(participant1, currentUserMock);
        Message msg3 = TestData.getMessage(participant2, currentUserMock);
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.getById(currentUserMock.getId())).thenReturn(currentUserMock);
        when(messageRepository.getUserMessages(currentUserMock.getId())).thenReturn(Arrays.asList(msg1, msg2, msg3));
        //when
        List<ConversationInfo> out = sut.getConversations(currentUserMock.getId());
        //then
        verify(userService).getById(currentUserMock.getId());
        verify(messageRepository).getUserMessages(currentUserMock.getId());
        Assertions.assertThat(out).hasSize(2)
                .allMatch(ci -> ci.getMessageCount() == 1 || ci.getMessageCount() == 2)
                .anyMatch(ci -> ci.getLastMessageTime().equals(msg2.getSendTime()))
                .anyMatch(ci -> ci.getParticipant().equals(participant1))
                .anyMatch(ci -> ci.getParticipant().equals(participant2));
    }

    @Test
    void should_throw_authorization_exception_when_is_not_current_users_conversation() {
        //given
        User currentUserMock = TestData.getAdmin();
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getConversation(currentUserMock.getId() - 1, currentUserMock.getId() + 1))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_conversation_and_mark_it_as_read() {
        //given
        User currentUserMock = TestData.getAdmin();
        final long id1 = currentUserMock.getId();
        final long id2 = id1 + 1;
        when(userDetailsService.getCurrentUser()).thenReturn(currentUserMock);
        //when
        sut.getConversation(id1, id2);
        //then
        verify(messageRepository).setConversationAsRead(id1, id2);
        verify(messageRepository).getConversation(id1, id2);

    }
}