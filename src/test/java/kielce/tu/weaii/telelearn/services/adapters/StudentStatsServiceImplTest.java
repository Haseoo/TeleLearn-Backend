package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.StudentStatsRecord;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.repositories.ports.CourseRepository;
import kielce.tu.weaii.telelearn.repositories.ports.StudentStatsRepository;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.servicedata.StudentStats;
import kielce.tu.weaii.telelearn.services.ports.TaskScheduleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class StudentStatsServiceImplTest {

    @Mock
    private StudentStatsRepository studentStatsRepository;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskScheduleService taskScheduleService;

    @InjectMocks
    private StudentStatsServiceImpl sut;

    @Test
    void should_add_record_learning() {
        //given
        Student student = TestData.getStudent();
        Task task = TestData.getTask(TestData.getCourse(TestData.getTeacher(), student));
        TaskScheduleRecord taskScheduleRecord = TestData.getTaskScheduleRecord(task, student);
        LocalTime startTime = LocalTime.of(11, 12);
        ArgumentCaptor<StudentStatsRecord> entityToAddCaptor = ArgumentCaptor.forClass(StudentStatsRecord.class);
        when(studentStatsRepository.getByScheduleId(taskScheduleRecord.getId())).thenReturn(Optional.empty());
        //when
        sut.recordOrUpdateLearning(taskScheduleRecord, startTime);
        //then
        verify(studentStatsRepository).save(entityToAddCaptor.capture());
        StudentStatsRecord entityToSave = entityToAddCaptor.getValue();
        Assertions.assertThat(entityToSave.getLearningTime()).isEqualTo(taskScheduleRecord.getLearningTime());
        Assertions.assertThat(entityToSave.getStartTime()).isEqualTo(startTime);
        Assertions.assertThat(entityToSave.getDate()).isEqualTo(taskScheduleRecord.getDate());
        Assertions.assertThat(entityToSave.getCourseId()).isEqualTo(taskScheduleRecord.getTask().getCourse().getId());
        Assertions.assertThat(entityToSave.getScheduleId()).isEqualTo(taskScheduleRecord.getId());
    }

    @Test
    void should_delete_record() {
        //given
        Student student = TestData.getStudent();
        Task task = TestData.getTask(TestData.getCourse(TestData.getTeacher(), student));
        TaskScheduleRecord taskScheduleRecord = TestData.getTaskScheduleRecord(task, student);
        StudentStatsRecord studentStatsRecord = TestData.getStudentStats(student);
        when(studentStatsRepository.getByScheduleId(taskScheduleRecord.getId())).thenReturn(Optional.of(studentStatsRecord));
        //when
        sut.deleteRecord(taskScheduleRecord);
        //then
        verify(studentStatsRepository).delete(studentStatsRecord);
    }

    @Test
    void should_return_student_stats() {
        //given
        LocalDate today = LocalDate.now();
        Student student = TestData.getStudent();
        Course course = TestData.getCourse(TestData.getTeacher(), student);
        course.setId(15L);
        Task imminentTask = TestData.getTask(course);
        imminentTask.setId(2L);
        imminentTask.setDueDate(today);
        Task farTask = TestData.getTask(course);
        farTask.setId(1L);
        farTask.setDueDate(today.plusDays(40));
        StudentStatsRecord studentStats1 = TestData.getStudentStats(student);
        studentStats1.setCourseId(course.getId());
        studentStats1.setDate(today);
        StudentStatsRecord studentStats2 = TestData.getStudentStats(student);
        studentStats2.setDate(today.minusDays(30));
        studentStats2.setCourseId(course.getId());
        TaskScheduleRecord taskScheduleRecord1 = TestData.getTaskScheduleRecord(imminentTask, student);
        TaskScheduleRecord taskScheduleRecord2 = TestData.getTaskScheduleRecord(farTask, student);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(studentStatsRepository.getStudentStat(student.getId())).thenReturn(Arrays.asList(studentStats1, studentStats2));
        when(taskScheduleService.getListForStudent(student.getId())).thenReturn(Arrays.asList(taskScheduleRecord1, taskScheduleRecord2));
        when(taskRepository.getStudentByTasksFromCurse(student.getId())).thenReturn(Arrays.asList(imminentTask, farTask));
        sut.setTaskScheduleService(taskScheduleService);
        //when
        StudentStats out = sut.getStudentStat(student.getId(), today);
        Assertions.assertThat(out.getAverageLearningTime())
                .isEqualTo(studentStats1.getLearningTime().plus(studentStats2.getLearningTime()).dividedBy(2));
        Assertions.assertThat(out.getHoursLearningStats()).containsKeys(studentStats1.getStartTime().getHour());
        Assertions.assertThat(out.getTaskTimeForWeek()).isEqualTo(imminentTask.getLearningTime());
        Assertions.assertThat(out.getPlannedTimeForWeek()).isEqualTo(taskScheduleRecord1.getPlannedTime());
        Assertions.assertThat(out.getLearningTimeForCourseSevenDays()).hasSize(1);
        Assertions.assertThat(out.getLearningTimeForCourseTotal()).hasSize(1);
    }

    @Test
    void should_throw_authorization_exception_when_subject_is_not_current_user() {
        //given
        Student student = TestData.getStudent();
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getAdmin());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getStudentStat(student.getId(), LocalDate.now())).isInstanceOf(AuthorizationException.class);
    }

}