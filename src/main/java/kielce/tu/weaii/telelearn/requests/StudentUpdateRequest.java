package kielce.tu.weaii.telelearn.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.Duration;

@Validated
@Getter
public class StudentUpdateRequest {
    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Podaj poprawny email")
    private String email;
    @NotBlank(message = "Imię nie może być puste")
    private String name;
    @NotBlank(message = "Nazwisko nie może być puste")
    private String surname;
    private String unit;
    private Duration dailyLearningTime;

    public StudentUpdateRequest(@JsonProperty(value = "email") String email,
                                @JsonProperty(value = "name") String name,
                                @JsonProperty(value = "surname") String surname,
                                @JsonProperty(value = "unit") String unit,
                                @Min(value = 0, message = "Nieprawidłowa liczba godzin") @JsonProperty(value = "hours", required = true) long hours,
                                @Range(min = 0, max = 60, message = "Nieprawidłowa liczba minut") @JsonProperty(value = "minutes", required = true) long minutes) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.unit = unit;
        this.dailyLearningTime = Duration.ofHours(hours).plus(Duration.ofMinutes(minutes));
    }
}
