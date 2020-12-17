package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.LearningTime;
import kielce.tu.weaii.telelearn.models.LearningTimeId;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.repositories.jpa.LearningTimeJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.LearningTimeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class LearningTimeRepositoryTest {

    @Autowired
    private LearningTimeJPARepository learningTimeJPARepository;
    @Autowired
    private StudentJPARepository studentJPARepository;

    @Autowired
    private LearningTimeRepository sut;

    @BeforeEach
    void setUp() {
        learningTimeJPARepository.deleteAll();
        studentJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        learningTimeJPARepository.deleteAll();
        studentJPARepository.deleteAll();
    }

    @Test
    @Transactional
    void should_save_learning_time() {
        //given
        Student student = studentJPARepository.saveAndFlush(TestData.getStudent());
        LocalDate date = LocalDate.now();
        LearningTime in = new LearningTime();
        in.setStudent(student);
        in.setTime(Duration.ZERO);
        in.setId(new LearningTimeId());
        in.getId().setDate(date);
        //when
        LearningTime out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(learningTimeJPARepository.findAll()).contains(in);

    }
}