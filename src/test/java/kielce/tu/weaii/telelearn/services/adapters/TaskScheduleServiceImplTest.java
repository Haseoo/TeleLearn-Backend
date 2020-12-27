package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.PreviousTaskNotCompleted;
import kielce.tu.weaii.telelearn.exceptions.courses.ScheduleForPastNotPossible;
import kielce.tu.weaii.telelearn.exceptions.courses.SchedulePlannedTimeUpdateNotPossible;
import kielce.tu.weaii.telelearn.exceptions.courses.UpdateLearningTimeNotPossible;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskScheduleRecord;
import kielce.tu.weaii.telelearn.repositories.ports.TaskScheduleRepository;
import kielce.tu.weaii.telelearn.requests.TimeSpanRequest;
import kielce.tu.weaii.telelearn.requests.courses.RecordLearningRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleTaskRequest;
import kielce.tu.weaii.telelearn.requests.courses.ScheduleUpdateRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.StudentStatsService;
import kielce.tu.weaii.telelearn.services.ports.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class TaskScheduleServiceImplTest {
    @Mock
    private TaskScheduleRepository taskScheduleRepository;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private TaskService taskService;
    @Mock
    private StudentService studentService;
    @Mock
    private StudentStatsService studentStatsService;

    @InjectMocks
    private TaskScheduleServiceImpl sut;

    @Test
    void should_throw_not_found_exception_when_record_doesnt_exist() {
        //given
        final long id = 1L;
        when(taskScheduleRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_authorization_exception_current_user_is_not_owner() {
        //given
        final long id = 1L;
        when(taskScheduleRepository.getById(id)).thenReturn(Optional.of(TestData.getTaskScheduleRecordService(TestData.getStudent())));
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getTeacher());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_and_return_record_by_id() {
        //given
        final long id = 1L;
        Student student = TestData.getStudent();
        TaskScheduleRecord taskScheduleRecord = TestData.getTaskScheduleRecordService(student);
        taskScheduleRecord.setId(id);
        when(taskScheduleRepository.getById(id)).thenReturn(Optional.of(taskScheduleRecord));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when
        TaskScheduleRecord out = sut.getById(id);
        //then
        verify(taskScheduleRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(taskScheduleRecord);
    }

    @Test
    void should_throw_authorization_when_student_is_not_current_user_on_task_schedule_list() {
        //given
        Student student = TestData.getStudent();
        final long id = student.getId();
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getAdmin());
        //then & then
        Assertions.assertThatThrownBy(() -> sut.getListForStudent(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_schedule_list_and_return_schedules_for_current_user_only() {
        //given
        final long id = 15L;
        Student student = TestData.getStudent();
        student.setId(id);
        TaskScheduleRecord record1 = TestData.getTaskScheduleRecordService(student);
        TaskScheduleRecord record2 = TestData.getTaskScheduleRecordService(TestData.getStudent());
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(taskScheduleRepository.getAll()).thenReturn(Arrays.asList(record1, record2));
        //when
        List<TaskScheduleRecord> out = sut.getListForStudent(id);
        //then
        verify(taskScheduleRepository).getAll();
        Assertions.assertThat(out).contains(record1).doesNotContain(record2);
    }

    @Test
    void should_ask_to_schedule() {
        //given
        Student student = TestData.getStudent();
        Task task = TestData.getTask(TestData.getCourse(TestData.getTeacher(), student));
        ScheduleTaskRequest request = TestData.getScheduleTaskRequest(task.getId(), student.getId());
        when(studentService.getById(request.getStudentId())).thenReturn(student);
        when(taskService.getById(request.getTaskId())).thenReturn(task);
        ArgumentCaptor<TaskScheduleRecord> entityToSaveCaptor = ArgumentCaptor.forClass(TaskScheduleRecord.class);
        //when
        sut.schedule(request, request.getDate());
        //then
        verify(studentService).getById(request.getStudentId());
        verify(taskService).getById(request.getTaskId());
        verify(taskScheduleRepository).save(entityToSaveCaptor.capture());
        TaskScheduleRecord entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getDate()).isEqualTo(request.getDate());
        Assertions.assertThat(entityToSave.getScheduleTime()).isEqualTo(request.getScheduleTime());
        Assertions.assertThat(entityToSave.getStudent()).isEqualTo(student);
        Assertions.assertThat(entityToSave.getTask()).isEqualTo(task);
        Assertions.assertThat(entityToSave.getLearningTime()).isZero();
    }

    @Test
    void should_throw_schedule__for_past_not_possible() {
        //given
        ScheduleTaskRequest request = TestData.getScheduleTaskRequest(0L, 0L);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.schedule(request, request.getDate().plusDays(5))).isInstanceOf(ScheduleForPastNotPossible.class);
    }

    @Test
    void should_ask_to_update_schedule() {
        //given
        Student student = TestData.getStudent();
        TaskScheduleRecord record = TestData.getTaskScheduleRecordService(student);
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(new TimeSpanRequest(1, 1), "12:12");
        when(taskScheduleRepository.getById(record.getId())).thenReturn(Optional.of(record));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        ArgumentCaptor<TaskScheduleRecord> entityToSaveCaptor = ArgumentCaptor.forClass(TaskScheduleRecord.class);
        //when
        sut.updateSchedule(record.getId(), request, record.getDate());
        //then
        verify(taskScheduleRepository).getById(record.getId());
        verify(taskScheduleRepository).save(entityToSaveCaptor.capture());
        TaskScheduleRecord entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getPlannedTime()).isEqualTo(record.getPlannedTime());
        Assertions.assertThat(entityToSave.getScheduleTime()).isEqualTo(record.getScheduleTime());

    }

    @Test
    void should_throw_update_schedule_not_possible() {
        //given
        Student student = TestData.getStudent();
        TaskScheduleRecord record = TestData.getTaskScheduleRecordService(student);
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(new TimeSpanRequest(1, 1), "12:12");
        when(taskScheduleRepository.getById(record.getId())).thenReturn(Optional.of(record));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateSchedule(record.getId(), request, record.getDate().plusDays(5))).isInstanceOf(SchedulePlannedTimeUpdateNotPossible.class);
    }

    @Test
    void should_update_learning_time() {
        //given
        Student student = TestData.getStudent();
        TaskScheduleRecord record = TestData.getTaskScheduleRecordService(student);
        when(taskScheduleRepository.getById(record.getId())).thenReturn(Optional.of(record));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 10));
        sut.setStudentStatsService(studentStatsService);
        ArgumentCaptor<TaskScheduleRecord> entityToSaveCaptor = ArgumentCaptor.forClass(TaskScheduleRecord.class);
        //when
        sut.updateLearningTime(record.getId(), request, record.getDate());
        //then
        verify(taskScheduleRepository).getById(record.getId());
        verify(taskScheduleRepository).save(entityToSaveCaptor.capture());
        verify(studentStatsService).recordOrUpdateLearning(any(), argThat(e -> e.equals(request.getStartTime())));
        TaskScheduleRecord entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getLearningTime()).isEqualTo(request.getDuration().getTimeSpan());
    }

    @Test
    void should_throw_update_learning_time_not_possible() {
        //given
        Student student = TestData.getStudent();
        TaskScheduleRecord record = TestData.getTaskScheduleRecordService(student);
        when(taskScheduleRepository.getById(record.getId())).thenReturn(Optional.of(record));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 10));
        sut.setStudentStatsService(studentStatsService);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateLearningTime(record.getId(), request, record.getDate().plusDays(1)))
                .isInstanceOf(UpdateLearningTimeNotPossible.class);

    }

    @Test
    void should_throw_previous_task_not_completed() {
        //given
        Student student = TestData.getStudent();
        TaskScheduleRecord record = TestData.getTaskScheduleRecordService(student);
        record.getTask().getPreviousTasks().add(TestData.getTask(record.getTask().getCourse()));
        when(taskScheduleRepository.getById(record.getId())).thenReturn(Optional.of(record));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        RecordLearningRequest request = new RecordLearningRequest("11:12", new TimeSpanRequest(1, 10));
        sut.setStudentStatsService(studentStatsService);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateLearningTime(record.getId(), request, record.getDate()))
                .isInstanceOf(PreviousTaskNotCompleted.class);

    }

    @Test
    void should_ask_to_delete_record() {
        //given
        final long id = 1L;
        Student student = TestData.getStudent();
        TaskScheduleRecord taskScheduleRecord = TestData.getTaskScheduleRecordService(student);
        taskScheduleRecord.setId(id);
        when(taskScheduleRepository.getById(id)).thenReturn(Optional.of(taskScheduleRecord));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        sut.setStudentStatsService(studentStatsService);
        //when
        sut.delete(id);
        //then
        verify(taskScheduleRepository).delete(taskScheduleRecord);
        verify(studentStatsService).deleteRecord(taskScheduleRecord);
    }

    @Test
    void should_ask_to_delete_student_schedules() {
        //given
        final long id = 1L;
        //when
        sut.deleteSchedulesForStudent(id);
        //then
        verify(taskScheduleRepository).deleteAllStudentRecord(id);
    }
}