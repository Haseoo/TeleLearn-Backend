package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Student;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "TASK_STUDENT")
public class TaskStudent implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "TASK_ID", referencedColumnName = "id")
    private Task task;

    @Id
    @ManyToOne
    @JoinColumn(name = "STUDENT_ID", referencedColumnName = "id")
    private Student student;

    @Column(nullable = false)
    private int taskCompletion = 0;

    @Column(nullable = false)
    private boolean toRepeat = false;
}
