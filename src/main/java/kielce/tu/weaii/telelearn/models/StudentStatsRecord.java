package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "STUDENT_STATS")
public class StudentStatsRecord {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Duration learningTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "studentId")
    private Student student;
}
