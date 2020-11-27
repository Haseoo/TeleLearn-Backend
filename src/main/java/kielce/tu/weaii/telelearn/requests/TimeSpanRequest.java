package kielce.tu.weaii.telelearn.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.Valid;
import java.time.Duration;

@Getter
@Valid
public class TimeSpanRequest {
    private Duration timeSpan;

    @JsonCreator
    @Valid
    public TimeSpanRequest(@JsonProperty(value = "hours", required = true) long hours,
                           @JsonProperty(value = "minutes", required = true) long minutes) {
        if (hours < 0 || hours > 23) {
            throw new IllegalArgumentException("Nieprawidłowa liczba godzin");
        }
        if (minutes < 0 || minutes >= 60) {
            throw new IllegalArgumentException("Nieprawidłowa liczba minut");
        }
        this.timeSpan = Duration.ofMinutes(minutes).plus(Duration.ofHours(hours));
    }
}
