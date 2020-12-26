package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.models.LearningTime;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.repositories.ports.LearningTimeRepository;
import kielce.tu.weaii.telelearn.requests.LearningTimeRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.servicedata.LearningTimeData;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class LearningTimeServiceImplTest {

    @Mock
    private LearningTimeRepository learningTimeRepository;
    @Mock
    private StudentService studentService;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;

    @InjectMocks
    private LearningTimeServiceImpl sut;

    @Test
    void should_throw_authorization_exception_when_current_user_in_not_subject_on_get_learning_time() {
        //given
        final long subjectId = 0L;
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getForStudent(subjectId)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_authorization_exception_when_current_user_in_not_subject_on_set_learning_time() {
        //given
        final long subjectId = 0L;
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        LearningTimeRequest request = TestData.getLearningTimeRequest(subjectId, LocalDate.of(2020, 12, 20));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.setLearningTime(request)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_to_save_learning_time() {
        //given
        Student student = TestData.getStudent();
        final long id = student.getId();
        when(studentService.getById(id)).thenReturn(student);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        LearningTimeRequest request = TestData.getLearningTimeRequest(id, LocalDate.of(2020, 12, 20));
        ArgumentCaptor<LearningTime> entityToSaveRequest = ArgumentCaptor.forClass(LearningTime.class);
        //when
        sut.setLearningTime(request);
        //then
        verify(studentService).getById(id);
        verify(learningTimeRepository).save(entityToSaveRequest.capture());
        LearningTime entityToSave = entityToSaveRequest.getValue();
        Assertions.assertThat(entityToSave.getTime()).isEqualTo(request.getTime().getTimeSpan());
        Assertions.assertThat(entityToSave.getId().getStudentId()).isEqualTo(id);
        Assertions.assertThat(entityToSave.getId().getDate()).isEqualTo(request.getDate());
    }

    @Test
    void should_ask_for_student_and_return_learning_time() {
        //given
        Student student = TestData.getStudent();
        LearningTime learningTime1 = new LearningTime();
        learningTime1.setDate(LocalDate.of(2020, 12, 20));
        learningTime1.setTime(Duration.ofMinutes(75));
        LearningTime learningTime2 = new LearningTime();
        learningTime2.setDate(LocalDate.of(2020, 12, 21));
        learningTime2.setTime(Duration.ofMinutes(30));
        student.getLearningTime().add(learningTime1);
        student.getLearningTime().add(learningTime2);
        final long id = student.getId();
        when(studentService.getById(id)).thenReturn(student);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when
        LearningTimeData out = sut.getForStudent(id);
        //then
        verify(studentService).getById(id);
        Assertions.assertThat(out.getDefaultLearningTime()).isEqualTo(student.getDailyLearningTime());
        Assertions.assertThat(out.getLearningTimeList()).contains(learningTime1, learningTime2);
    }
}