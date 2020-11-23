package kielce.tu.weaii.telelearn.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Valid
@Getter
public class LearningTimeRequest {
    @NotNull(message = "Należy określić ucznia.")
    private Long studentId;
    @NotNull(message = "Należy określić datę.")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;
    @NotNull(message = "Należy określić czas.")
    @Valid
    private TimeSpanRequest time;

    public LearningTimeRequest(@JsonProperty(value = "studentId", required = true) Long studentId,
                               @JsonProperty(value = "date", required = true) LocalDate date,
                               @JsonProperty(value = "time", required = true) TimeSpanRequest time) {
        this.studentId = studentId;
        this.date = date;
        this.time = time;
    }
}
