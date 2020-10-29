package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.Message;
import kielce.tu.weaii.telelearn.repositories.jpa.MessageJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepositoryImpl extends BaseCRUDRepositoryImpl<Message> implements MessageRepository{
    private final MessageJPARepository messageJPARepository;

    public MessageRepositoryImpl(MessageJPARepository messageJPARepository) {
        super(messageJPARepository);
        this.messageJPARepository = messageJPARepository;
    }

    @Override
    public List<Message> getUserMessages(Long userId) {
        return messageJPARepository.findUserMessages(userId);
    }

    @Override
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messageJPARepository.findConversationMessages(user1Id, user2Id);
    }

    @Override
    public void setConversationAsRead(Long receiverId, Long senderId) {
        messageJPARepository.setConversationAsRead(receiverId, senderId);
    }
}
