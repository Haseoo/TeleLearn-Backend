package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.courses.Comment;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.models.courses.PostVisibility;
import kielce.tu.weaii.telelearn.views.UserView;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Value
public class PostView {
    Long id;
    String content;
    UserView author;
    LocalDateTime publicationTime;
    PostVisibility postVisibility;
    boolean commentingAllowed;
    List<AttachmentView> attachments;
    int commentCount;

    public static PostView from(Post model) {
        return new PostView(model.getId(),
                model.getContent(),
                UserView.from(model.getAuthor(), false),
                model.getPublicationTime(),
                model.getPostVisibility(),
                model.isCommentingAllowed(),
                model.getAttachments().stream().map(AttachmentView::form).collect(toList()),
                model.getComments().size());
    }
}
