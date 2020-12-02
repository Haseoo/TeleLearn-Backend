package kielce.tu.weaii.telelearn.repositories.jpa;

import kielce.tu.weaii.telelearn.models.courses.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
@Profile("dev")
public interface TaskJPARepositoryDev extends TaskJPARepository {
    @Query(value = "SELECT t.ID, t.DESCRIPTION, t.DUE_DATE, t.LEARNING_TIME, t.NAME, t.COURSE_ID FROM TASKS t JOIN COURSES c ON t.COURSE_ID = c.ID JOIN COURSE_STUDENT cs ON cs.COURSE_ID = c.ID WHERE cs.STUDENT_ID = ?1",
            nativeQuery = true)
    List<Task> getStudentByTasksFromCurse(Long studentId);
}
