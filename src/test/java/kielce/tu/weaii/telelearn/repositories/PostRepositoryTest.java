package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.repositories.jpa.CourseJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.PostJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.PostRepository;
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
class PostRepositoryTest {

    @Autowired
    private CourseJPARepository courseJPARepository;
    @Autowired
    private TeacherJPARepository teacherJPARepository;
    @Autowired
    private StudentJPARepository studentJPARepository;
    @Autowired
    private PostJPARepository postJPARepository;

    @Autowired
    private PostRepository sut;

    Student student;
    Teacher teacher;
    Course course;

    @BeforeEach
    void setUp() {
        postJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        teacherJPARepository.deleteAll();

        student = studentJPARepository.saveAndFlush(TestData.getStudent());
        teacher = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        course = courseJPARepository.saveAndFlush(TestData.getCourse(teacher, student));
    }

    @AfterEach
    void tearDown() {
        postJPARepository.deleteAll();
        courseJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        teacherJPARepository.deleteAll();

        student = null;
        teacher = null;
        course = null;
    }

    @Test
    @Transactional
    void should_save_post() {
        //given
        Post in = TestData.getPost(course, student);
        //when
        Post out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(postJPARepository.findAll()).contains(in);
    }

    @Test
    @Transactional
    void should_return_post_by_id() {
        //given
        Post searched = postJPARepository.saveAndFlush(TestData.getPost(course, teacher));
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_all_posts() {
        //given
        Post searched1 = postJPARepository.saveAndFlush(TestData.getPost(course, teacher));
        Post searched2 = postJPARepository.saveAndFlush(TestData.getPost(course, student));
        //when & then
        Assertions.assertThat(sut.getAll()).contains(searched1, searched2);
    }

    @Test
    @Transactional
    void should_delete_post() {
        //given
        Post post1 = postJPARepository.saveAndFlush(TestData.getPost(course, teacher));
        Post post2 = postJPARepository.saveAndFlush(TestData.getPost(course, student));
        //when
        sut.delete(post2);
        //then
        Assertions.assertThat(postJPARepository.findAll()).contains(post1).doesNotContain(post2);
    }
}