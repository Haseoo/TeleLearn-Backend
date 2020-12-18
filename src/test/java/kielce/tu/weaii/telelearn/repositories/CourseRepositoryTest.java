package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.repositories.jpa.CourseJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.CourseRepository;
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
class CourseRepositoryTest {

    @Autowired
    private CourseJPARepository courseJPARepository;
    @Autowired
    private TeacherJPARepository teacherJPARepository;
    @Autowired
    private StudentJPARepository studentJPARepository;

    @Autowired
    private CourseRepository sut;

    private Teacher teacher;
    private Student student;

    @BeforeEach
    void setUp() {
        courseJPARepository.deleteAll();
        teacherJPARepository.deleteAll();
        teacher = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        student = studentJPARepository.saveAndFlush(TestData.getStudent());
    }

    @AfterEach
    void tearDown() {
        courseJPARepository.deleteAll();
        teacherJPARepository.deleteAll();
        teacher = null;
        student = null;
    }

    @Test
    @Transactional
    void should_add_course() {
        //given
        Course in = TestData.getCourse(teacher, student);
        //when
        Course out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(courseJPARepository.findAll()).contains(in);
    }

    @Test
    @Transactional
    void should_get_course_by_id() {
        //given
        Course searched = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_all_courses() {
        //given
        Course searched1 = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
        Course searched2 = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
        //when & then
        Assertions.assertThat(sut.getAll()).contains(searched1, searched2);
    }

    @Test
    @Transactional
    void should_delete_course() {
        //given
        Course course1 = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
        Course course2 = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
        //when
        sut.delete(course1);
        //then
        Assertions.assertThat(courseJPARepository.findAll()).doesNotContain(course1).contains(course2);
    }

}