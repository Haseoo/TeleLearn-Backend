package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.servicedata.StudentStats;
import kielce.tu.weaii.telelearn.views.TimeVew;
import lombok.Value;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Value
public class StudentStatsView {
    TimeVew taskTimeForWeek;
    TimeVew plannedTimeForWeek;
    TimeVew learningTimeForWeek;
    Map<Long, TimeVew> learningTimeForCourseSevenDays;
    Map<Long, TimeVew> learningTimeForCourseTotal;
    Map<Integer, Long> hoursLearningStats;
    TimeVew averageLearningTime;

    public static StudentStatsView from(StudentStats studentStats) {
        Map<Long, TimeVew> learningTimeForCourseSevenDays = new HashMap<>();
        Map<Long, TimeVew> learningTimeForCourseTotal = new HashMap<>();
        for (Map.Entry<Long, Duration> entry : studentStats.getLearningTimeForCourseSevenDays().entrySet()) {
            learningTimeForCourseSevenDays.put(entry.getKey(), TimeVew.form(entry.getValue()));
        }
        for (Map.Entry<Long, Duration> entry : studentStats.getLearningTimeForCourseTotal().entrySet()) {
            learningTimeForCourseTotal.put(entry.getKey(), TimeVew.form(entry.getValue()));
        }
        return new StudentStatsView(TimeVew.form(studentStats.getTaskTimeForWeek()),
                TimeVew.form(studentStats.getPlannedTimeForWeek()),
                TimeVew.form(studentStats.getLearningTimeForWeek()),
                learningTimeForCourseSevenDays,
                learningTimeForCourseTotal,
                studentStats.getHoursLearningStats(),
                TimeVew.form(studentStats.getAverageLearningTime()));
    }
}
