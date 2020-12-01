package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Attachment;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "TASKS")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Duration learningTime;

    @Column(nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "COURSE_ID")
    private Course course;

    @ManyToMany()
    @JoinTable(name = "TASK_LINKS",
            joinColumns = @JoinColumn(name = "TASK_ID"),
            inverseJoinColumns = @JoinColumn(name = "PREVIOUS_TASK_ID"))
    private List<Task> previousTasks;

    @ManyToMany(mappedBy = "previousTasks")
    private List<Task> nextTasks;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "task")
    private List<Attachment> attachments;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "task")
    private List<TaskStudent> students;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "task")
    private List<TaskScheduleRecord> planRecords;

    public TaskStudent getStudentRecordOrNull(Long studentId) {
        return students.stream().filter(entry -> entry.getStudent().getId().equals(studentId)).findAny().orElse(null);
    }
}
