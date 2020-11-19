package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.servicedata.TaskSchemeRecord;
import lombok.Value;

@Value
public class TaskSchemeRecordView {
    TaskViewForStudent task;
    long totalLearningTimeHours;
    long totalLearningTimeMinutes;
    long totalPlannedLearningTimeHours;
    long totalPlannedLearningTimeMinutes;

    public static TaskSchemeRecordView from(TaskSchemeRecord record, long studentId) {
        return new TaskSchemeRecordView(
                TaskViewForStudent.from(record.getTask(), studentId),
                record.getTotalLearningTime().toHours(),
                record.getTotalLearningTime().minusHours(record.getTotalLearningTime().toHours()).toMinutes(),
                record.getTotalPlannedLearningTime().toHours(),
                record.getTotalPlannedLearningTime().minusHours(record.getTotalPlannedLearningTime().toHours()).toMinutes()
        );
    }
}
