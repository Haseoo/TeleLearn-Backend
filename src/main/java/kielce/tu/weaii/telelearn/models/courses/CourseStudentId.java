package kielce.tu.weaii.telelearn.models.courses;

import lombok.Data;

import java.io.Serializable;

@Data
public class CourseStudentId implements Serializable {
    private long course;
    private long student;
}
