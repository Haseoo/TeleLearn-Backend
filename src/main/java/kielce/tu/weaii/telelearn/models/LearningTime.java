package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "LEARNING_TIME")
@IdClass(LearningTimeId.class)
public class LearningTime {
    @Id
    @ManyToOne
    @JoinColumn(name = "STUDENT_ID", referencedColumnName = "id", nullable = false)
    private Student student;

    @Id
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration time;
}
