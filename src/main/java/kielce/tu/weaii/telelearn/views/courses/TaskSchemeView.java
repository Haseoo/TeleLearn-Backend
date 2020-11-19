package kielce.tu.weaii.telelearn.views.courses;

import kielce.tu.weaii.telelearn.servicedata.TaskScheme;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TaskSchemeView {
    public static Map<Object, List<TaskSchemeRecordView>> from(TaskScheme scheme, long studentId) {
        Map<Object, List<TaskSchemeRecordView>> map = new HashMap<>();
        map.put("delayedTask", scheme.getDelayedTasks().stream().map(record -> TaskSchemeRecordView.from(record, studentId)).collect(Collectors.toList()));
        map.put("taskToRepeat", scheme.getTaskToRepeat().stream().map(record -> TaskSchemeRecordView.from(record, studentId)).collect(Collectors.toList()));
        for (LocalDate key : scheme.getTasksForDay().keySet()) {
            String newKey = key.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            map.put(newKey, scheme.getTasksForDay().get(key).stream().map(record -> TaskSchemeRecordView.from(record, studentId)).collect(Collectors.toList()));
        }
        return map;
    }
}
