package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Message;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.repositories.jpa.MessageJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.UserJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.MessageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class MessageRepositoryTest {

    @Autowired
    private UserJPARepository userJPARepository;
    @Autowired
    private MessageJPARepository messageJPARepository;

    @Autowired
    private MessageRepository sut;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        messageJPARepository.deleteAll();
        userJPARepository.deleteAll();
        user1 = userJPARepository.saveAndFlush(TestData.getStudent());
        user2 = userJPARepository.saveAndFlush(TestData.getTeacher());
    }

    @AfterEach
    void tearDown() {
        messageJPARepository.deleteAll();
        userJPARepository.deleteAll();
        user1 = null;
        user2 = null;
    }

    @Test
    @Transactional
    void should_save_message() {
        //given
        Message in = TestData.getMessage(user1, user2);
        //when
        Message out = sut.save(in);
        //then
        Assertions.assertThat(out).isEqualTo(in);
        Assertions.assertThat(messageJPARepository.findAll()).contains(out);
    }

    @Test
    @Transactional
    void should_return_user_messages() {
        //given
        User other = TestData.getAdmin();
        other.setId(null);
        other = userJPARepository.saveAndFlush(other);
        Message message1 = messageJPARepository.saveAndFlush(TestData.getMessage(user1, user2));
        Message message2 = messageJPARepository.saveAndFlush(TestData.getMessage(user1, other));
        Message message3 = messageJPARepository.saveAndFlush(TestData.getMessage(user2, other));
        //when & then
        Assertions.assertThat(sut.getUserMessages(user1.getId()))
                .contains(message1, message2)
                .doesNotContain(message3);
    }

    @Test
    @Transactional
    void should_return_conversation() {
        //given
        User other = TestData.getAdmin();
        other.setId(null);
        other = userJPARepository.saveAndFlush(other);
        Message message1 = messageJPARepository.saveAndFlush(TestData.getMessage(user1, user2));
        Message message2 = messageJPARepository.saveAndFlush(TestData.getMessage(user1, other));
        Message message3 = messageJPARepository.saveAndFlush(TestData.getMessage(user2, other));
        //when
        Assertions.assertThat(sut.getConversation(user1.getId(), user2.getId()))
                .contains(message1)
                .doesNotContain(message2, message3);
    }
}