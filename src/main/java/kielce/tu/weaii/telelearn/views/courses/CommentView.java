package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.Comment;
import kielce.tu.weaii.telelearn.views.UserView;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentView {
    Long id;
    UserView author;
    LocalDateTime publicationTime;
    String content;

    public static CommentView from(Comment model) {
        return new CommentView(model.getId(),
                UserView.from(model.getAuthor(), false),
                model.getPublicationTime(),
                model.getContent());
    }
}
