package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class LearningTimeId implements Serializable {
    private Student student;
    private LocalDate date;
}
