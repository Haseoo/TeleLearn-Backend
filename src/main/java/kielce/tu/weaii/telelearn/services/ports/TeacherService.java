package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherUpdateRequest;

import java.util.List;

public interface TeacherService {
    Teacher getById(Long id);

    Teacher add(TeacherRegisterRequest request);

    Teacher update(Long id, TeacherUpdateRequest request);

    void delete(Long id);

    List<Course> getCourses(Long id);

}
