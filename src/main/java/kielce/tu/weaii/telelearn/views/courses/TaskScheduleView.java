package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.views.StudentView;
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
    long plannedTimeHours;
    long plannedTimeMinutes;
    long learningTimeHours;
    long learningTimeMinutes;
    StudentView student;
    TaskView task;

    public static TaskScheduleView form(TaskScheduleRecord model) {
        return new TaskScheduleView(model.getId(),
                model.getDate(),
                model.getPlannedTime().toHours(),
                model.getPlannedTime().minusHours(model.getPlannedTime().toHours()).toMinutes(),
                model.getLearningTime().toHours(),
                model.getLearningTime().minusHours(model.getLearningTime().toHours()).toMinutes(),
                StudentView.from(model.getStudent(), false),
                TaskView.from(model.getTask()));
    }

    public static Map<String, List<TaskScheduleView>> form(List<TaskScheduleRecord> taskSchedule) {
        Map<String, List<TaskScheduleView>> returnMap = new HashMap<>();
        Map<LocalDate, List<TaskScheduleView>> mapWithLocalDate =  taskSchedule.stream()
                .map(TaskScheduleView::form)
                .collect(Collectors.groupingBy(TaskScheduleView::getDate));
        for(Map.Entry<LocalDate, List<TaskScheduleView>> entry : mapWithLocalDate.entrySet()) {
            returnMap.put(entry.getKey().format(DATE_FORMATTER_FOR_MAP_KEY), entry.getValue());
        }
        return returnMap;
    }
}
