package kielce.tu.weaii.telelearn.repositories.ports;

import kielce.tu.weaii.telelearn.models.LearningTime;

public interface LearningTimeRepository {
    LearningTime save(LearningTime learningTime);
}
