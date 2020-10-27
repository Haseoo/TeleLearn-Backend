package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;


public interface StudentService {
    Student getById(Long id);

    Student add(StudentRegisterRequest request);

    Student update(Long id, StudentUpdateRequest request);

    void delete(Long id);
}
