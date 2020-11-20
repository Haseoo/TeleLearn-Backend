package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.servicedata.TaskStudentSummaryRecord;
import lombok.Value;

@Value
public class TaskToScheduleRecordView {
    TaskViewForStudent task;
    long totalLearningTimeHours;
    long totalLearningTimeMinutes;
    long totalPlannedLearningTimeHours;
    long totalPlannedLearningTimeMinutes;

    public static TaskToScheduleRecordView from(TaskStudentSummaryRecord record, long studentId) {
        return new TaskToScheduleRecordView(
                TaskViewForStudent.from(record.getTask(), studentId),
                record.getTotalLearningTime().toHours(),
                record.getTotalLearningTime().minusHours(record.getTotalLearningTime().toHours()).toMinutes(),
                record.getTotalPlannedLearningTime().toHours(),
                record.getTotalPlannedLearningTime().minusHours(record.getTotalPlannedLearningTime().toHours()).toMinutes()
        );
    }
}
