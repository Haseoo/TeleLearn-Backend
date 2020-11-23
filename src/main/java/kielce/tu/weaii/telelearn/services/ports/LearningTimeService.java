package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.LearningTime;
import kielce.tu.weaii.telelearn.requests.LearningTimeRequest;
import kielce.tu.weaii.telelearn.servicedata.LearningTimeData;


public interface LearningTimeService {
    LearningTimeData getForStudent(Long studentId);

    LearningTime setLearningTime(LearningTimeRequest request);
}
