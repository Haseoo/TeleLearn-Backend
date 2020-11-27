package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kielce.tu.weaii.telelearn.requests.TimeSpanRequest;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalTime;

import static kielce.tu.weaii.telelearn.utilities.Constants.TIME_FORMATTER;
import static kielce.tu.weaii.telelearn.utilities.Utils.isStringNullOrEmpty;

@Getter
@Valid
public class ScheduleUpdateRequest {
    @Valid
    @NotNull(message = "Należy określić czas.")
    private Duration plannedTime;

    private LocalTime scheduleTime;

    public ScheduleUpdateRequest(@JsonProperty(value = "duration", required = true) TimeSpanRequest duration,
                                 @JsonProperty(value = "startTime") String startTime) {
        this.plannedTime = duration.getTimeSpan();
        if (isStringNullOrEmpty(startTime)) {
            this.scheduleTime = null;
        } else {
            this.scheduleTime = LocalTime.parse(startTime, TIME_FORMATTER);
        }
    }
}
