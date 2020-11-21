package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Valid
@Getter
public class RecordLearningRequest {
    private LocalTime startTime;

    @Valid
    private TimeSpanRequest duration;

    @JsonCreator
    @Valid
    public RecordLearningRequest(@NotBlank(message = "Należy podać czas rozpoczęcia.") @JsonProperty(value = "startTime", required = true) String startTime,
                                 @JsonProperty(value = "duration", required = true) TimeSpanRequest duration) {
        this.startTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        this.duration = duration;
    }
}
