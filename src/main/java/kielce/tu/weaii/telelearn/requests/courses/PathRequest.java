package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
@Getter
public class PathRequest {
    @NotBlank(message = "Nie podano nazwy ścieżki.")
    private String name;
    private String description;
    @NotNull(message = "Nie podano kursu dla ścieżki.")
    private Long courseId;

    public PathRequest(@JsonProperty(value = "name", required = true) String name,
                       @JsonProperty(value = "description") String description,
                       @JsonProperty(value = "courseId", required = true) Long courseId) {
        this.name = name;
        this.description = description;
        this.courseId = courseId;
    }
}
