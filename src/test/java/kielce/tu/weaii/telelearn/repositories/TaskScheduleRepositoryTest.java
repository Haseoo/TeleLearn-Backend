package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.repositories.jpa.*;
import kielce.tu.weaii.telelearn.repositories.ports.TaskScheduleRepository;
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
class TaskScheduleRepositoryTest {

    @Autowired
    private CourseJPARepository courseJPARepository;
    @Autowired
    private TeacherJPARepository teacherJPARepository;
    @Autowired
    private StudentJPARepository studentJPARepository;
    @Autowired
    private TaskJPARepository taskJPARepository;
    @Autowired
    private TaskScheduleJPARepository taskScheduleJPARepository;

    @Autowired
    private TaskScheduleRepository sut;

    private Student student;
    private Task task;

    @BeforeEach
    void setUp() {
        taskScheduleJPARepository.deleteAll();
        taskJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        teacherJPARepository.deleteAll();
        studentJPARepository.deleteAll();

        student = studentJPARepository.saveAndFlush(TestData.getStudent());
        Course course = courseJPARepository.saveAndFlush(TestData.getCourse(teacherJPARepository.saveAndFlush(TestData.getTeacher()), student));
        task = taskJPARepository.saveAndFlush(TestData.getTask(course));
    }

    @AfterEach
    void tearDown() {
        taskScheduleJPARepository.deleteAll();
        taskJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        teacherJPARepository.deleteAll();
        studentJPARepository.deleteAll();

        student = null;
        task = null;
    }

    @Test
    @Transactional
    void should_save_save_schedule_record() {
        //given
        TaskScheduleRecord in = TestData.getTaskScheduleRecord(task, student);
        //when
        TaskScheduleRecord out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(taskScheduleJPARepository.findAll()).contains(in);
    }

    @Test
    @Transactional
    void should_return_schedule_record_by_id() {
        //given
        TaskScheduleRecord searched = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_all_schedule_records() {
        //given
        TaskScheduleRecord searched1 = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        TaskScheduleRecord searched2 = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        //when & then
        Assertions.assertThat(sut.getAll()).contains(searched1, searched2);
    }

    @Test
    @Transactional
    void should_delete_record() {
        //given
        TaskScheduleRecord record = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        TaskScheduleRecord toDelete = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        //when
        sut.delete(toDelete);
        //then
        Assertions.assertThat(sut.getAll()).contains(record).doesNotContain(toDelete);
    }

    @Test
    @Transactional
    void should_delete_all_student_record() {
        //given
        Student otherStudent = studentJPARepository.saveAndFlush(TestData.getStudent());
        TaskScheduleRecord record1 = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        TaskScheduleRecord record2 = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, student));
        TaskScheduleRecord record3 = taskScheduleJPARepository.saveAndFlush(TestData.getTaskScheduleRecord(task, otherStudent));
        //when
        sut.deleteAllStudentRecord(student.getId());
        //then
        Assertions.assertThat(taskScheduleJPARepository.findAll()).contains(record3).doesNotContain(record1, record2);

    }
}