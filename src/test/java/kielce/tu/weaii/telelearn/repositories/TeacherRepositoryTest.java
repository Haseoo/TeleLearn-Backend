package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.repositories.jpa.TeacherJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
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
class TeacherRepositoryTest {

    @Autowired
    private TeacherJPARepository teacherJPARepository;

    @Autowired
    private TeacherRepository sut;

    @BeforeEach
    void setUp() {
        teacherJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        teacherJPARepository.deleteAll();
    }

    @Test
    @Transactional
    void should_save_teacher() {
        //given
        Teacher in = TestData.getTeacher();
        //when
        Teacher out = sut.save(in);
        //then
        Assertions.assertThat(in.getTitle()).isEqualTo(out.getTitle());
        Assertions.assertThat(in.getUnit()).isEqualTo(out.getUnit());
    }

    @Test
    @Transactional
    void should_return_teacher_with_id() {
        //when
        Teacher searched = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_list_of_teacher() {
        //when
        Teacher teacher1 = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        Teacher teacher2 = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        //when & then
        Assertions.assertThat(sut.getAll()).contains(teacher1, teacher2);
    }

    @Test
    @Transactional
    void should_delete_teachers() {
        //when
        Teacher teacher1 = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        Teacher teacher2 = teacherJPARepository.saveAndFlush(TestData.getTeacher());
        //when
        sut.delete(teacher1);
        //then
        Assertions.assertThat(teacherJPARepository.findAll()).doesNotContain(teacher1).contains(teacher2);
    }
}