package kielce.tu.weaii.telelearn.repositories.ports;

import kielce.tu.weaii.telelearn.models.LearningTime;

import java.util.List;

public interface LearningTimeRepository {
    List<LearningTime> getByStudent(Long studentId);

    LearningTime save(LearningTime learningTime);
}
