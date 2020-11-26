package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;


@Entity
@Data
@Table(name = "LEARNING_TIME")
public class LearningTime implements Serializable {
    @EmbeddedId
    private LearningTimeId id;

    @ManyToOne
    @JoinColumn(name = "STUDENT_ID", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @MapsId("studentId")
    private Student student;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration time;
}
