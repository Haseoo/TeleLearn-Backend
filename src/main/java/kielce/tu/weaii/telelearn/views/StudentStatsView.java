package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.servicedata.StudentStats;
import kielce.tu.weaii.telelearn.views.courses.CourseBriefView;
import lombok.Value;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Value
public class StudentStatsView {
    TimeVew taskTimeForWeek;
    TimeVew plannedTimeForWeek;
    TimeVew learningTimeForWeek;
    Map<CourseBriefView, TimeVew> learningTimeForCourseSevenDays;
    Map<CourseBriefView, TimeVew> learningTimeForCourseTotal;
    Map<Integer, Long> hoursLearningStats;
    TimeVew averageLearningTime;

    public static StudentStatsView from(StudentStats studentStats) {
        Map<CourseBriefView, TimeVew> learningTimeForCourseSevenDays = new HashMap<>();
        Map<CourseBriefView, TimeVew> learningTimeForCourseTotal = new HashMap<>();
        for (Map.Entry<Course, Duration> entry : studentStats.getLearningTimeForCourseSevenDays().entrySet()) {
            CourseBriefView newKey = (entry.getKey() != null) ? CourseBriefView.from(entry.getKey()) : null;
            learningTimeForCourseSevenDays.put(newKey, TimeVew.form(entry.getValue()));
        }
        for (Map.Entry<Course, Duration> entry : studentStats.getLearningTimeForCourseTotal().entrySet()) {
            CourseBriefView newKey = (entry.getKey() != null) ? CourseBriefView.from(entry.getKey()) : null;
            learningTimeForCourseTotal.put(newKey, TimeVew.form(entry.getValue()));
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
