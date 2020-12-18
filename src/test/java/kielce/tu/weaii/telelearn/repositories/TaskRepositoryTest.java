package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.repositories.jpa.CourseJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.TaskJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class TaskRepositoryTest {
    @Autowired
    private CourseJPARepository courseJPARepository;
    @Autowired
    private TeacherJPARepository teacherJPARepository;
    @Autowired
    private StudentJPARepository studentJPARepository;
    @Autowired
    private TaskJPARepository taskJPARepository;

    @Autowired
    private TaskRepository sut;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        taskJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        teacherJPARepository.deleteAll();

        student = studentJPARepository.saveAndFlush(TestData.getStudent());
        course = courseJPARepository.saveAndFlush(TestData.getCourse(teacherJPARepository.saveAndFlush(TestData.getTeacher()), student));
    }

    @AfterEach
    void tearDown() {
        taskJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        teacherJPARepository.deleteAll();

        student = null;
        course = null;
    }

    @Test
    @Transactional
    void should_save_task() {
        //given
        Task prev = taskJPARepository.saveAndFlush(TestData.getTask(course));
        Task in = TestData.getTask(course);
        in.getPreviousTasks().add(prev);
        //when
        Task out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(taskJPARepository.findAll()).contains(in);
    }

    @Test
    @Transactional
    void should_return_task_by_id() {
        //given
        Task searched = taskJPARepository.saveAndFlush(TestData.getTask(course));
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_all_tasks() {
        //given
        Task searched1 = taskJPARepository.saveAndFlush(TestData.getTask(course));
        Task searched2 = taskJPARepository.saveAndFlush(TestData.getTask(course));
        //when & then
        Assertions.assertThat(sut.getAll()).contains(searched1, searched2);
    }

    @Test
    @Transactional
    void should_delete_task() {
        //given
        Task task = taskJPARepository.saveAndFlush(TestData.getTask(course));
        Task toDelete = TestData.getTask(course);
        toDelete.getPreviousTasks().add(task);
        toDelete = taskJPARepository.saveAndFlush(toDelete);
        //when
        sut.delete(toDelete);
        //then
        Assertions.assertThat(sut.getAll()).contains(task).doesNotContain(toDelete);
    }

    @Test
    @Transactional
    void should_return_student_tasks() {
        //given
        Task task1 = taskJPARepository.saveAndFlush(TestData.getTask(course));
        Task task2 = taskJPARepository.saveAndFlush(TestData.getTask(course));
        Course otherCourse = courseJPARepository.saveAndFlush(TestData.getCourse(
                teacherJPARepository.saveAndFlush(TestData.getTeacher()),
                studentJPARepository.saveAndFlush(TestData.getStudent()))
        );
        Task task3 = taskJPARepository.saveAndFlush(TestData.getTask(otherCourse));
        //when & then
        Assertions.assertThat(sut.getStudentByTasksFromCurse(student.getId())).contains(task1, task2)
                .doesNotContain(task3);
    }
}