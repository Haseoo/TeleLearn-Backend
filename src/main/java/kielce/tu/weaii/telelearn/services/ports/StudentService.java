package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;

import java.util.List;


public interface StudentService {
    Student getById(Long id);

    Student add(StudentRegisterRequest request);

    Student update(Long id, StudentUpdateRequest request);

    void delete(Long id);

    List<Course> getCourses(Long id);
}
