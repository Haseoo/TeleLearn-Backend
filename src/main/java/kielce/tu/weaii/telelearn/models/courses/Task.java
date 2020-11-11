package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Attachment;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "TASKS")
public class Task {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Lob
    private String description;

    @Column(nullable = false)
    private int learningTimeHours;

    @Column(nullable = false)
    private int learningTimeMinutes;

    @Column(nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "pathId")
    private Path path;

    @ManyToMany
    @JoinTable(name = "TASK_LINKS",
            joinColumns = @JoinColumn(name = "taskId"),
            inverseJoinColumns = @JoinColumn(name = "prevoiusTaskId"))
    private List<Task> previousTasks;

    @ManyToMany(mappedBy = "previousTasks")
    private List<Task> nextTasks;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "task")
    private List<Attachment> attachments;
}
