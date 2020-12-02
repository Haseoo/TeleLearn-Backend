package kielce.tu.weaii.telelearn.views;

import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.servicedata.StudentStats;
import lombok.Value;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Value
public class StudentStatsView {
    TimeVew taskTimeForWeek;
    TimeVew plannedTimeForWeek;
    TimeVew learningTimeForWeek;
    Set<Map.Entry<String, TimeVew>> learningTimeForCourseSevenDays;
    Set<Map.Entry<String, TimeVew>> learningTimeForCourseTotal;
    Map<Integer, Long> hoursLearningStats;
    TimeVew averageLearningTime;

    public static StudentStatsView from(StudentStats studentStats) {
        Set<Map.Entry<String, TimeVew>> learningTimeForCourseSevenDays = new HashSet<>();
        Set<Map.Entry<String, TimeVew>> learningTimeForCourseTotal = new HashSet<>();
        for (Map.Entry<Course, Duration> entry : studentStats.getLearningTimeForCourseSevenDays().entrySet()) {
            convertMap(learningTimeForCourseSevenDays, entry);
        }
        for (Map.Entry<Course, Duration> entry : studentStats.getLearningTimeForCourseTotal().entrySet()) {
            convertMap(learningTimeForCourseTotal, entry);
        }
        return new StudentStatsView(TimeVew.form(studentStats.getTaskTimeForWeek()),
                TimeVew.form(studentStats.getPlannedTimeForWeek()),
                TimeVew.form(studentStats.getLearningTimeForWeek()),
                learningTimeForCourseSevenDays,
                learningTimeForCourseTotal,
                studentStats.getHoursLearningStats(),
                TimeVew.form(studentStats.getAverageLearningTime()));
    }

    private static void convertMap(Set<Map.Entry<String, TimeVew>> learningTimeForCourseSevenDays, Map.Entry<Course, Duration> entry) {
        String newKey = (entry.getKey() != null) ? entry.getKey().getName() : "usuniętę";
        Map.Entry<String, TimeVew> newEntry = new AbstractMap.SimpleEntry<>(newKey, TimeVew.form(entry.getValue()));
        learningTimeForCourseSevenDays.add(newEntry);
    }
}
