package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.requests.courses.CourseRequest;

public interface CourseService {
    Course getById(Long id);

    Course add(CourseRequest request);

    Course update(Long id, CourseRequest request);

    void delete(Long id);

    boolean signUpStudent(Long courseId, Long studentId);

    void acceptStudent(Long courseId, Long studentId);

    void signOutStudent(Long courseId, Long studentId);
}
