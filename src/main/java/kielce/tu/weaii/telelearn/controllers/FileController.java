package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.services.ports.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final AttachmentService attachmentService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable Long id) {
        Attachment attachment = attachmentService.getById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, attachment.getFileType())
                .body(attachment.getData());
    }
}