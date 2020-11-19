package kielce.tu.weaii.telelearn.servicedata;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class TaskScheme {
    private List<TaskSchemeRecord> delayedTasks;
    private List<TaskSchemeRecord> taskToRepeat;
    private Map<LocalDate, List<TaskSchemeRecord>> tasksForDay;
}
