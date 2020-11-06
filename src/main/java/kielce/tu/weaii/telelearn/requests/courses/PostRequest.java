package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kielce.tu.weaii.telelearn.models.courses.PostVisibility;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Valid
@Data
public class PostRequest {
    @NotNull(message = "Nie podano id kursu.")
    private Long courseId;
    @NotBlank(message = "Post musi mieć zawartość")
    private String content;
    @NotNull(message = "Nie określono widoczności posta.")
    private PostVisibility postVisibility;
    @NotNull(message = "Nie określono możliwości komentowania.")
    private boolean commentingAllowed;
    private List<Long> attachmentIdsToDelete = new ArrayList<>();
}
