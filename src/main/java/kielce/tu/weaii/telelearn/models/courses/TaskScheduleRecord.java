package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Student;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "TASK_SCHEDULE")
public class TaskScheduleRecord {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration plannedTime;

    @Column(nullable = false)
    private Duration learningTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "studentId")
    private Student student;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "taskId")
    private Task task;
}
