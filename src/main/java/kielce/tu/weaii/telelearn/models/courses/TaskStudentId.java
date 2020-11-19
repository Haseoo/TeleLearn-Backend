package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Student;
import lombok.Data;

@Data
public class TaskStudentId {
    private Task task;
    private Student student;
}
