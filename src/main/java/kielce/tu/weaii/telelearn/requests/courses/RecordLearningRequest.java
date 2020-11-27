package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import kielce.tu.weaii.telelearn.requests.TimeSpanRequest;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

import static kielce.tu.weaii.telelearn.utilities.Constants.TIME_FORMATTER;

@Valid
@Getter
public class RecordLearningRequest {
    private LocalTime startTime;

    @Valid
    @NotNull(message = "Należy określić czas.")
    private TimeSpanRequest duration;

    @JsonCreator
    @Valid
    public RecordLearningRequest(@NotBlank(message = "Należy podać czas rozpoczęcia.") @JsonProperty(value = "startTime", required = true) String startTime,
                                 @JsonProperty(value = "duration", required = true) TimeSpanRequest duration) {
        this.startTime = LocalTime.parse(startTime, TIME_FORMATTER);
        this.duration = duration;
    }
}
