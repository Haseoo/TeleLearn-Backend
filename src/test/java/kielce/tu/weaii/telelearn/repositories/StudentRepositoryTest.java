package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.StudentRepository;
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
class StudentRepositoryTest {

    @Autowired
    private StudentJPARepository studentJPARepository;

    @Autowired
    private StudentRepository sut;

    @BeforeEach
    void setUp() {
        studentJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        studentJPARepository.deleteAll();
    }

    @Test
    @Transactional
    void should_save_teacher() {
        //given
        Student in = TestData.getStudent();
        //when
        Student out = sut.save(in);
        //then
        Assertions.assertThat(in.getDailyLearningTime()).isEqualTo(out.getDailyLearningTime());
        Assertions.assertThat(in.getUnit()).isEqualTo(out.getUnit());
    }

    @Test
    @Transactional
    void should_return_teacher_with_id() {
        //when
        Student searched = studentJPARepository.saveAndFlush(TestData.getStudent());
        //when & then
        Assertions.assertThat(sut.getById(searched.getId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void should_return_list_of_teacher() {
        //when
        Student student1 = studentJPARepository.saveAndFlush(TestData.getStudent());
        Student student2 = studentJPARepository.saveAndFlush(TestData.getStudent());
        //when & then
        Assertions.assertThat(sut.getAll()).contains(student1, student2);
    }

    @Test
    @Transactional
    void should_delete_teachers() {
        //when
        Student student1 = studentJPARepository.saveAndFlush(TestData.getStudent());
        Student student2 = studentJPARepository.saveAndFlush(TestData.getStudent());
        //when
        sut.delete(student1);
        //then
        Assertions.assertThat(studentJPARepository.findAll()).doesNotContain(student1).contains(student2);
    }
}