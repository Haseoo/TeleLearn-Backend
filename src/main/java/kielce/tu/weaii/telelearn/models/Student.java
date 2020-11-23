package kielce.tu.weaii.telelearn.models;

import kielce.tu.weaii.telelearn.models.courses.CourseStudent;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.models.courses.TaskStudent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Duration;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "STUDENTS")
public class Student extends User {
    @Column(nullable = false)
    private Duration dailyLearningTime;

    @Column(columnDefinition = "TEXT")
    private String unit;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "student")
    private List<CourseStudent> courses;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "student")
    private List<TaskStudent> tasks;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "student")
    private List<TaskScheduleRecord> planRecords;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "student")
    private List<LearningTime> learningTime;

    public static final Duration DEFAULT_DAILY_LEARNING_TIME = Duration.ofHours(3).plus(Duration.ofMinutes(30));
}
