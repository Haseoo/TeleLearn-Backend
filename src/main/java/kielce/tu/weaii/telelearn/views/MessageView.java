package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.Message;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class MessageView {
    UserView receiver;
    UserView sender;
    String content;
    LocalDateTime sendTime;

    public static MessageView from(Message model) {
        return new MessageView(UserView.from(model.getReceiver(), false),
                UserView.from(model.getSender(), false),
                model.getContent(),
                model.getSendTime());
    }
}
