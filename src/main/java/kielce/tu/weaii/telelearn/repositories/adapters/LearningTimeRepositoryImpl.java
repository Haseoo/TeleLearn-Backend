package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.LearningTime;
import kielce.tu.weaii.telelearn.repositories.jpa.LearningTimeJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.LearningTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LearningTimeRepositoryImpl implements LearningTimeRepository {
    private final LearningTimeJPARepository jpaRepository;

    @Override
    public List<LearningTime> getByStudent(Long studentId) {
        return jpaRepository.findAllByStudentId(studentId);
    }

    @Override
    public LearningTime save(LearningTime learningTime) {
        return jpaRepository.saveAndFlush(learningTime);
    }
}
