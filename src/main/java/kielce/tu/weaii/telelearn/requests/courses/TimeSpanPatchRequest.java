package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.Duration;

@Getter
public class TimeSpanPatchRequest {
    private Duration timeSpan;

    @JsonCreator
    @Valid
    public TimeSpanPatchRequest(@Min(value = 0, message = "Nieprawidłowa liczba godzin") @JsonProperty(value = "hours", required = true) long hours,
                                @Range(min = 0, max = 60, message = "Nieprawidłowa liczba minut") @JsonProperty(value = "minutes", required = true) long minutes) {
        this.timeSpan = Duration.ofMinutes(minutes).plus(Duration.ofHours(hours));
    }
}
