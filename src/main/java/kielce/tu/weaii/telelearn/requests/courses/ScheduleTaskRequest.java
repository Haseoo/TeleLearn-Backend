package kielce.tu.weaii.telelearn.requests.courses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static kielce.tu.weaii.telelearn.utilities.Constants.TIME_FORMATTER;
import static kielce.tu.weaii.telelearn.utilities.Utils.isStringNullOrEmpty;

@Valid
@Getter
public class ScheduleTaskRequest {
    private Long taskId;
    private Long studentId;
    @JsonIgnore
    private Duration plannedTime;
    @JsonIgnore
    private LocalDate date;
    @JsonIgnore
    private LocalTime scheduleTime;

    @Valid
    @JsonCreator
    public ScheduleTaskRequest(@JsonProperty(value = "taskId", required = true) Long taskId,
                               @JsonProperty(value = "studentId", required = true) Long studentId,
                               @JsonProperty(value = "date", required = true) String date,
                               @JsonProperty(value = "startTime") String startTime,
                               @Min(value = 0, message = "Nieprawidłowa liczba godzin") @JsonProperty(value = "hours", required = true) long hours,
                               @Range(min = 0, max = 60, message = "Nieprawidłowa liczba minut") @JsonProperty(value = "minutes", required = true) long minutes) {
        this.taskId = taskId;
        this.studentId = studentId;
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.plannedTime = Duration.ofHours(hours).plus(Duration.ofMinutes(minutes));
        if (!isStringNullOrEmpty(startTime)) {
            this.scheduleTime = LocalTime.parse(startTime, TIME_FORMATTER);
        }
    }
}
