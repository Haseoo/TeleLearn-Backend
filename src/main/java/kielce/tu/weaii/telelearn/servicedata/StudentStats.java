package kielce.tu.weaii.telelearn.servicedata;

import lombok.Data;

import java.time.Duration;
import java.util.Map;

@Data
public class StudentStats {
    private Duration taskTimeForWeek;
    private Duration plannedTimeForWeek;
    private Duration learningTimeForWeek;
    private Map<Long, Duration> learningTimeForCourseSevenDays;
    private Map<Long, Duration> learningTimeForCourseTotal;
    private Map<Integer, Long> hoursLearningStats;
    private Duration averageLearningTime;
}
