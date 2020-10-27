package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.StudentRepository;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepositoryImpl extends BaseCRUDRepositoryImpl<Student> implements StudentRepository {
    private final StudentJPARepository jpaRepository;

    public StudentRepositoryImpl(StudentJPARepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }
}
