package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.MultipartFileMock;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.PathWouldHaveCycle;
import kielce.tu.weaii.telelearn.exceptions.courses.TaskMustBeCompleted;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.models.courses.TaskStudent;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;
import kielce.tu.weaii.telelearn.requests.courses.TaskProgressPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRepeatPatchRequest;
import kielce.tu.weaii.telelearn.requests.courses.TaskRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.servicedata.TaskStudentSummary;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static kielce.tu.weaii.telelearn.utilities.Constants.TASK_COMPLETED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private UserService userService;
    @Mock
    private CourseService courseService;
    @Mock
    private StudentService studentService;

    @InjectMocks
    private TaskServiceImpl sut;

    private Student student;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void beforeEach() {
        student = TestData.getStudent();
        student.setId(123L);
        teacher = TestData.getTeacher();
        teacher.setId(312L);
        course = TestData.getCourse(teacher, student);
        course.setId(2137L);
    }

    @Test
    void should_not_found_exception_when_task_doesnt_exist() {
        //given
        final Long id = 1L;
        when(taskRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_authentication_exception_when_teacher_is_not_course_owner() {
        //given
        Task task = TestData.getTask(course);
        when(taskRepository.getById(task.getId())).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getTeacher());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(task.getId())).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_authentication_exception_when_student_is_not_course_participant() {
        //given
        Task task = TestData.getTask(course);
        when(taskRepository.getById(task.getId())).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(task.getId())).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_and_return_course_by_id() {
        //given
        Task task = TestData.getTask(course);
        final Long id = 5L;
        task.setId(id);
        when(taskRepository.getById(id)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when
        Task out = sut.getById(id);
        //then
        verify(taskRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(task);
    }

    @Test
    void should_delete_task() {
        //given
        Task taskToDelete = TestData.getTask(course);
        final Long id = 5L;
        taskToDelete.setId(id);
        when(taskRepository.getById(id)).thenReturn(Optional.of(taskToDelete));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        Task pTask = TestData.getTask(course);
        pTask.getNextTasks().add(taskToDelete);
        taskToDelete.getPreviousTasks().add(pTask);
        Task nTask = TestData.getTask(course);
        nTask.getPreviousTasks().add(taskToDelete);
        taskToDelete.getNextTasks().add(nTask);
        //when
        sut.delete(id);
        //then
        verify(taskRepository).delete(taskToDelete);
        Assertions.assertThat(nTask.getPreviousTasks()).doesNotContain(taskToDelete);
        Assertions.assertThat(pTask.getNextTasks()).doesNotContain(taskToDelete);
    }

    @Test
    void should_throw_authorization_exception_on_progress_update_when_current_user_is_not_subject() {
        //given
        final long taskId = 1L;
        TaskProgressPatchRequest request = new TaskProgressPatchRequest(0L, 50);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateProgress(taskId, request)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_add_task_progress() {
        //given
        final long taskId = 1L;
        final int progress = 50;
        TaskProgressPatchRequest request = new TaskProgressPatchRequest(student.getId(), progress);
        Task task = TestData.getTask(course);
        task.setId(taskId);
        when(taskRepository.getById(taskId)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(studentService.getById(student.getId())).thenReturn(student);
        ArgumentCaptor<Task> entityToSaveCaptor = ArgumentCaptor.forClass(Task.class);
        //when
        sut.updateProgress(taskId, request);
        //then
        verify(taskRepository).save(entityToSaveCaptor.capture());
        Task entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getStudentRecordOrNull(student.getId()))
                .isNotNull().matches(e -> e.getTaskCompletion() == progress);
    }

    @Test
    void should_update_task_progress() {
        //given
        final long taskId = 1L;
        final int progress = 50;
        TaskProgressPatchRequest request = new TaskProgressPatchRequest(student.getId(), progress);
        Task task = TestData.getTask(course);
        task.setId(taskId);
        TaskStudent ts = new TaskStudent();
        ts.setStudent(student);
        ts.setTask(task);
        ts.setTaskCompletion(progress / 2);
        task.getStudents().add(ts);
        when(taskRepository.getById(taskId)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        ArgumentCaptor<Task> entityToSaveCaptor = ArgumentCaptor.forClass(Task.class);
        //when
        sut.updateProgress(taskId, request);
        //then
        verify(taskRepository).save(entityToSaveCaptor.capture());
        Task entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getStudentRecordOrNull(student.getId()))
                .isNotNull().matches(e -> e.getTaskCompletion() == progress);
    }

    @Test
    void should_throw_authorization_exception_on_set_to_repeat_when_current_user_is_not_subject() {
        //given
        final long taskId = 1L;
        TaskRepeatPatchRequest request = new TaskRepeatPatchRequest(0L, false);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when  & then
        Assertions.assertThatThrownBy(() -> sut.updateTaskRepeat(taskId, request)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_task_must_be_completed_exception_when_progress_is_not_100() {
        //given
        final long taskId = 1L;
        final int progress = 50;
        Task task = TestData.getTask(course);
        task.setId(taskId);
        TaskStudent ts = new TaskStudent();
        ts.setStudent(student);
        ts.setTask(task);
        ts.setTaskCompletion(progress);
        task.getStudents().add(ts);
        TaskRepeatPatchRequest request = new TaskRepeatPatchRequest(student.getId(), true);
        when(taskRepository.getById(taskId)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateTaskRepeat(taskId, request)).isInstanceOf(TaskMustBeCompleted.class);
    }

    @Test
    void should_throw_task_must_be_completed_exception_when_progress_is_not_null() {
        //given
        final long taskId = 1L;
        Task task = TestData.getTask(course);
        task.setId(taskId);
        TaskRepeatPatchRequest request = new TaskRepeatPatchRequest(student.getId(), true);
        when(taskRepository.getById(taskId)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updateTaskRepeat(taskId, request)).isInstanceOf(TaskMustBeCompleted.class);
    }

    @Test
    void should_mark_task_to_repeat() {
        //given
        final long taskId = 1L;
        Task task = TestData.getTask(course);
        task.setId(taskId);
        TaskStudent ts = new TaskStudent();
        ts.setStudent(student);
        ts.setTask(task);
        ts.setTaskCompletion(TASK_COMPLETED);
        task.getStudents().add(ts);
        TaskRepeatPatchRequest request = new TaskRepeatPatchRequest(student.getId(), true);
        when(taskRepository.getById(taskId)).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        ArgumentCaptor<Task> entityToSaveCaptor = ArgumentCaptor.forClass(Task.class);
        //when
        sut.updateTaskRepeat(taskId, request);
        //then
        verify(taskRepository).save(entityToSaveCaptor.capture());
        Task entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getStudentRecordOrNull(student.getId())).matches(e -> e.isToRepeat() == request.getToRepeat());
    }

    @Test
    void should_return_student_task_summary() {
        //given
        LocalDate today = LocalDate.now();
        Task delayedTask = TestData.getTask(course);
        delayedTask.setDueDate(today.minusDays(1));
        Task taskToRepeat = TestData.getTask(course);
        TaskStudent ts = new TaskStudent();
        ts.setStudent(student);
        ts.setTask(taskToRepeat);
        ts.setTaskCompletion(TASK_COMPLETED);
        ts.setToRepeat(true);
        taskToRepeat.getStudents().add(ts);
        Task averageTask = TestData.getTask(course);
        averageTask.setDueDate(today);
        when(userService.isCurrentUserOrAdmin(student.getId())).thenReturn(true);
        when(taskRepository.getStudentByTasksFromCurse(student.getId()))
                .thenReturn(Arrays.asList(delayedTask, taskToRepeat, averageTask));
        //when
        TaskStudentSummary out = sut.getStudentByTasksFromCurse(student.getId(), today);
        //then
        Assertions.assertThat(out.getTasksForDay().get(averageTask.getDueDate()))
                .isNotNull()
                .anyMatch(e -> e.getTask().equals(averageTask));
        Assertions.assertThat(out.getDelayedTasks()).anyMatch(e -> e.getTask().equals(delayedTask));
        Assertions.assertThat(out.getTaskToRepeat()).anyMatch(e -> e.getTask().equals(taskToRepeat));
    }

    @Test
    void should_ask_to_add_task() throws IOException {
        //given
        Task pTask = TestData.getTask(course);
        pTask.setId(20L);
        MultipartFileMock attachment = new MultipartFileMock(new byte[]{21, 37});
        TaskRequest request = TestData.getTaskRequest(course.getId());
        request.getPreviousTaskIds().add(pTask.getId());
        when(courseService.getById(course.getId())).thenReturn(course);
        when(taskRepository.getById(pTask.getId())).thenReturn(Optional.of(pTask));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        ArgumentCaptor<Task> entityToSaveCaptor = ArgumentCaptor.forClass(Task.class);
        //when
        sut.add(request, Arrays.asList(attachment));
        //then
        verify(taskRepository).save(entityToSaveCaptor.capture());
        Task entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getPreviousTasks()).contains(pTask);
        Assertions.assertThat(entityToSave.getAttachments())
                .anyMatch(e -> e.getFileName().equals(attachment.getOriginalFilename()) &&
                        e.getTask().equals(entityToSave) &&
                        e.getFileType().equals(attachment.getContentType()) &&
                        e.getPost() == null &&
                        e.getAttachmentData().get(0).getData().equals(attachment.getBytes()));
        Assertions.assertThat(entityToSave.getDueDate()).isEqualTo(request.getDueDate());
        Assertions.assertThat(entityToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(entityToSave.getDescription()).isEqualTo(request.getDescription());
        Assertions.assertThat(entityToSave.getCourse()).isEqualTo(course);
        Assertions.assertThat(entityToSave.getLearningTime())
                .isEqualTo(Duration.ofMinutes(request.getLearningTimeMinutes())
                        .plus(Duration.ofHours(request.getLearningTimeHours())));
    }

    @Test
    void should_ask_to_update_task() throws IOException {
        //given
        Task task = TestData.getTask(course);
        task.setAttachments(new ArrayList<>());
        task.setId(20L);
        Attachment attachmentToDelete = TestData.getAttachment();
        attachmentToDelete.setId(25L);
        task.getAttachments().add(attachmentToDelete);
        Task pTask = TestData.getTask(course);
        pTask.setId(19L);
        MultipartFileMock attachmentToAdd = new MultipartFileMock(new byte[]{21, 37});
        TaskRequest request = TestData.getTaskRequest(course.getId());
        request.getAttachmentIdsToDelete().add(attachmentToDelete.getId());
        request.getPreviousTaskIds().add(pTask.getId());
        when(taskRepository.getById(pTask.getId())).thenReturn(Optional.of(pTask));
        when(taskRepository.getById(task.getId())).thenReturn(Optional.of(task));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        ArgumentCaptor<Task> entityToSaveCaptor = ArgumentCaptor.forClass(Task.class);
        //when
        sut.update(task.getId(), request, Arrays.asList(attachmentToAdd));
        //then
        verify(taskRepository).save(entityToSaveCaptor.capture());
        Task entityToSave = entityToSaveCaptor.getValue();
        Assertions.assertThat(entityToSave.getPreviousTasks()).contains(pTask);
        Assertions.assertThat(entityToSave.getAttachments())
                .anyMatch(e -> e.getFileName().equals(attachmentToAdd.getOriginalFilename()) &&
                        e.getTask().equals(entityToSave) &&
                        e.getFileType().equals(attachmentToAdd.getContentType()) &&
                        e.getPost() == null &&
                        e.getAttachmentData().get(0).getData().equals(attachmentToAdd.getBytes()));
        Assertions.assertThat(entityToSave.getDueDate()).isEqualTo(request.getDueDate());
        Assertions.assertThat(entityToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(entityToSave.getDescription()).isEqualTo(request.getDescription());
        Assertions.assertThat(entityToSave.getCourse()).isEqualTo(course);
        Assertions.assertThat(entityToSave.getLearningTime())
                .isEqualTo(Duration.ofMinutes(request.getLearningTimeMinutes())
                        .plus(Duration.ofHours(request.getLearningTimeHours())));
        //Assertions.assertThat(entityToSave.getAttachments()).doesNotContain(attachmentToDelete);
    }

    @Test
    void should_throw_path_would_have_cycle_exception_on_update_task() throws IOException {
        //given
        Task pTask = TestData.getTask(course);
        pTask.setId(20L);
        Task ppTask = TestData.getTask(course);
        ppTask.setId(19L);
        pTask.getPreviousTasks().add(ppTask);
        TaskRequest request = TestData.getTaskRequest(course.getId());
        request.getPreviousTaskIds().add(pTask.getId());
        when(taskRepository.getById(pTask.getId())).thenReturn(Optional.of(pTask));
        when(taskRepository.getById(ppTask.getId())).thenReturn(Optional.of(ppTask));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when
        Assertions.assertThatThrownBy(() -> sut.update(ppTask.getId(), request, null)).isInstanceOf(PathWouldHaveCycle.class);
    }

}