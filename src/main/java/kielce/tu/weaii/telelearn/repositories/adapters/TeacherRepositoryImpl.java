package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherRepositoryImpl extends BaseCRUDRepositoryImpl<Teacher> implements TeacherRepository {
    private final TeacherJPARepository jpaRepository;

    public TeacherRepositoryImpl(TeacherJPARepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }
}
