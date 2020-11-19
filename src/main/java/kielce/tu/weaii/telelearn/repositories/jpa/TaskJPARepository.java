package kielce.tu.weaii.telelearn.repositories.jpa;

import kielce.tu.weaii.telelearn.models.courses.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskJPARepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT t.ID, t.DESCRIPTION, t.DUE_DATE, t.LEARNING_TIME, t.NAME, t.COURSE_ID FROM TASKS t JOIN COURSES c ON t.COURSE_ID = c.ID JOIN COURSE_STUDENT cs ON cs.COURSE_ID = c.ID WHERE cs.STUDENT_ID = ?1",
            nativeQuery = true)
    List<Task> getStudentByTasksFromCurse(Long studentId);
}
