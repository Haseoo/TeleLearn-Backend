package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.StudentStatsRecord;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentJPARepository;
import kielce.tu.weaii.telelearn.repositories.jpa.StudentStatsJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.StudentStatsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class StudentStatsRepositoryTest {
    @Autowired
    private StudentStatsJPARepository studentStatsJPARepository;

    @Autowired
    private StudentJPARepository studentJPARepository;

    @Autowired
    private StudentStatsRepository sut;

    private Student student;

    @BeforeEach
    void setUp() {
        studentStatsJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        student = studentJPARepository.saveAndFlush(TestData.getStudent());
    }

    @AfterEach
    void tearDown() {
        studentStatsJPARepository.deleteAll();
        studentJPARepository.deleteAll();
        student = null;
    }

    @Test
    @Transactional
    void should_return_list_of_schedule_records() {
        //given
        Student student2 = studentJPARepository.saveAndFlush(TestData.getStudent());
        StudentStatsRecord record1 = studentStatsJPARepository.saveAndFlush(TestData.getStudentStats(student));
        StudentStatsRecord record2 = studentStatsJPARepository.saveAndFlush(TestData.getStudentStats(student2));
        //when & then
        Assertions.assertThat(sut.getStudentStat(student.getId())).contains(record1).doesNotContain(record2);

    }

    @Test
    @Transactional
    void should_save_stats() {
        //given
        StudentStatsRecord in = TestData.getStudentStats(student);
        //when
        StudentStatsRecord out = sut.save(in);
        //then
        Assertions.assertThat(in).isEqualTo(out);
        Assertions.assertThat(studentStatsJPARepository.findAll()).contains(in);
    }

    @Test
    @Transactional
    void should_return_record_by_schedule_id() {
        //given
        StudentStatsRecord searched = studentStatsJPARepository.saveAndFlush(TestData.getStudentStats(student));
        //when & then
        List<StudentStatsRecord> foo = studentStatsJPARepository.findAll();
        Assertions.assertThat(sut.getByScheduleId(searched.getScheduleId())).isPresent().containsSame(searched);
    }

    @Test
    @Transactional
    void delete() {
        //given
        StudentStatsRecord record1 = studentStatsJPARepository.saveAndFlush(TestData.getStudentStats(student));
        StudentStatsRecord record2 = studentStatsJPARepository.saveAndFlush(TestData.getStudentStats(student));
        //when
        sut.delete(record1);
        //then
        Assertions.assertThat(studentStatsJPARepository.findAll()).doesNotContain(record1).contains(record2);
    }
}