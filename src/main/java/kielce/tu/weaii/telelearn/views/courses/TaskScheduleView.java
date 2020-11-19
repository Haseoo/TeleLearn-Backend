package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.views.StudentView;
import lombok.Value;

import java.time.LocalDate;


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
                model.getPlannedTime().toMinutes(),
                model.getLearningTime().toHours(),
                model.getLearningTime().toMinutes(),
                StudentView.from(model.getStudent(), false),
                TaskView.from(model.getTask()));
    }
}
