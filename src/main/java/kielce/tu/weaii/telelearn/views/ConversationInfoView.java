package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.servicedata.ConversationInfo;
import lombok.Value;

@Value
public class ConversationInfoView {
    UserView participant;
    long messageCount;
    long unreadMessageCount;

    public static ConversationInfoView from(ConversationInfo conversationInfo) {
        return new ConversationInfoView(UserView.from(conversationInfo.getParticipant(), false),
                conversationInfo.getMessageCount(),
                conversationInfo.getUnreadMessageCount());
    }
}
