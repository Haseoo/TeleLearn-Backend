package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.Attachment;
import lombok.Value;

@Value
public class AttachmentView {
    Long id;
    String fileName;

    public static AttachmentView form(Attachment model) {
        return new AttachmentView(model.getId(), model.getFileName());
    }
}
