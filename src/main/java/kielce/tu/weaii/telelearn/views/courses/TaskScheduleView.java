package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.views.StudentView;
import kielce.tu.weaii.telelearn.views.TimeVew;
import lombok.Value;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kielce.tu.weaii.telelearn.utilities.Constants.DATE_FORMATTER_FOR_MAP_KEY;


@Value
public class TaskScheduleView {
    long id;
    LocalDate date;
    TimeVew plannedTime;
    TimeVew learningTime;
    StudentView student;
    TaskView task;

    public static TaskScheduleView form(TaskScheduleRecord model) {
        return new TaskScheduleView(model.getId(),
                model.getDate(),
                TimeVew.form(model.getPlannedTime()),
                TimeVew.form(model.getLearningTime()),
                StudentView.from(model.getStudent(), false),
                TaskView.from(model.getTask()));
    }

    public static Map<String, List<TaskScheduleView>> form(List<TaskScheduleRecord> taskSchedule) {
        Map<String, List<TaskScheduleView>> returnMap = new HashMap<>();
        Map<LocalDate, List<TaskScheduleView>> mapWithLocalDate = taskSchedule.stream()
                .map(TaskScheduleView::form)
                .collect(Collectors.groupingBy(TaskScheduleView::getDate));
        for (Map.Entry<LocalDate, List<TaskScheduleView>> entry : mapWithLocalDate.entrySet()) {
            returnMap.put(entry.getKey().format(DATE_FORMATTER_FOR_MAP_KEY), entry.getValue());
        }
        return returnMap;
    }
}