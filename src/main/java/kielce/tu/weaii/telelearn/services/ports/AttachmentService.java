package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.Attachment;

public interface AttachmentService {
    Attachment getById(Long id);
}
