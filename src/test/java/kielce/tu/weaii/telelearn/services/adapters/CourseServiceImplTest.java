package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.StudentAlreadyEnrolled;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.CourseStudent;
import kielce.tu.weaii.telelearn.repositories.ports.CourseRepository;
import kielce.tu.weaii.telelearn.requests.courses.CourseRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.TaskScheduleService;
import kielce.tu.weaii.telelearn.services.ports.TeacherService;
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

import static org.mockito.Mockito.*;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private TeacherService teacherService;
    @Mock
    private StudentService studentService;
    @Mock
    private UserService userService;
    @Mock
    private TaskScheduleService taskScheduleService;

    @InjectMocks
    private CourseServiceImpl sut;

    @BeforeEach
    void beforeEach() {
        sut.setTaskScheduleService(taskScheduleService);
    }

    @Test
    void should_throw_exception_when_current_teacher_is_not_owner() {
        //given
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        Teacher currentUserMock = TestData.getTeacher();
        currentUserMock.setId(69L);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_exception_when_current_student_is_not_participant() {
        //given
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        Student currentUserMock = TestData.getStudent();
        currentUserMock.setId(69L);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_and_return_course_when_current_user_is_participant() {
        //given
        Student currentUserMock = TestData.getStudent();
        currentUserMock.setId(69L);
        Course course = TestData.getCourse(TestData.getTeacher(), currentUserMock);
        final long id = 10L;
        course.setId(id);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when
        Course out = sut.getById(id);
        //then
        verify(courseRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(course);
    }

    @Test
    void should_ask_for_and_return_course_when_current_user_is_owner() {
        //given
        Teacher currentUserMock = TestData.getTeacher();
        currentUserMock.setId(69L);
        Course course = TestData.getCourse(currentUserMock, TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(true);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when
        Course out = sut.getById(id);
        //then
        verify(courseRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(course);
    }

    @Test
    void should_throw_not_found_exception_when_course_does_not_exist() {
        //given
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_not_found_exception_when_course_get_course() {
        //given
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getCourse(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_ask_for_and_return_course() {
        //given
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        final long id = 10L;
        course.setId(id);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when
        Course out = sut.getCourse(id);
        //then
        verify(courseRepository).getById(id);
        Assertions.assertThat(out).isEqualTo(course);
    }

    @Test
    void should_ask_for_save_course() {
        //given
        Teacher owner = TestData.getTeacher();
        CourseRequest request = TestData.getCourseRequest(owner.getId());
        when(userService.isCurrentUserOrAdmin(owner.getId())).thenReturn(true);
        when(teacherService.getById(owner.getId())).thenReturn(owner);
        ArgumentCaptor<Course> entityToAddCaptor = ArgumentCaptor.forClass(Course.class);
        //when
        sut.add(request);
        //then
        verify(courseRepository).save(entityToAddCaptor.capture());
        Course entityToAdd = entityToAddCaptor.getValue();
        Assertions.assertThat(entityToAdd.getOwner()).isEqualTo(owner);
        Assertions.assertThat(entityToAdd.getName()).isEqualTo(request.getName());
        Assertions.assertThat(entityToAdd.getWelcomePageHtmlContent()).isEqualTo(request.getWelcomePageHtmlContent());
        Assertions.assertThat(entityToAdd.isAutoAccept()).isEqualTo(request.isAutoAccept());
        Assertions.assertThat(entityToAdd.isPublicCourse()).isEqualTo(request.isPublicCourse());
        Assertions.assertThat(entityToAdd.isStudentsAllowedToPost()).isEqualTo(request.isStudentsAllowedToPost());
    }

    @Test
    void should_throw_authorization_exception_when_current_user_in_not_owner() {
        //given
        Teacher owner = TestData.getTeacher();
        owner.setId(15L);
        CourseRequest request = TestData.getCourseRequest(owner.getId());
        when(userService.isCurrentUserOrAdmin(owner.getId())).thenReturn(false);
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getTeacher());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.add(request)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_update_course() {
        //given
        final long id = 1L;
        Teacher owner = TestData.getTeacher();
        CourseRequest request = TestData.getCourseRequest(owner.getId());
        Course course = TestData.getCourse(owner, TestData.getStudent());
        when(userService.isCurrentUserOrAdmin(owner.getId())).thenReturn(true);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        when(userServiceDetails.getCurrentUser()).thenReturn(owner);
        ArgumentCaptor<Course> entityToAddCaptor = ArgumentCaptor.forClass(Course.class);
        //when
        sut.update(id, request);
        //then
        verify(courseRepository).getById(id);
        verify(courseRepository).save(entityToAddCaptor.capture());
        Course entityToAdd = entityToAddCaptor.getValue();
        Assertions.assertThat(entityToAdd.getOwner()).isEqualTo(owner);
        Assertions.assertThat(entityToAdd.getName()).isEqualTo(request.getName());
        Assertions.assertThat(entityToAdd.getWelcomePageHtmlContent()).isEqualTo(request.getWelcomePageHtmlContent());
        Assertions.assertThat(entityToAdd.isAutoAccept()).isEqualTo(request.isAutoAccept());
        Assertions.assertThat(entityToAdd.isPublicCourse()).isEqualTo(request.isPublicCourse());
        Assertions.assertThat(entityToAdd.isStudentsAllowedToPost()).isEqualTo(request.isStudentsAllowedToPost());
    }

    @Test
    void should_ask_for_delete_course() {
        //given
        Teacher currentUserMock = TestData.getTeacher();
        Student student = TestData.getStudent();
        currentUserMock.setId(69L);
        Course course = TestData.getCourse(currentUserMock, student);
        final long id = 10L;
        course.setId(id);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(true);
        when(courseRepository.getById(id)).thenReturn(java.util.Optional.of(course));
        //when
        sut.delete(id);
        //then
        verify(taskScheduleService).deleteSchedulesForStudent(student.getId());
        verify(courseRepository).delete(course);
    }

    @Test
    void should_throw_authorization_exception_on_sign_up_student() {
        //given
        User currentUserMock = TestData.getStudent();
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.signUpStudent(0L, 0L)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_return_true_on_sign_up_student() {
        //given
        Student currentUserMock = TestData.getStudent();
        currentUserMock.setId(15L);
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        when(courseRepository.getById(course.getId())).thenReturn(java.util.Optional.of(course));
        when(studentService.getById(currentUserMock.getId())).thenReturn(currentUserMock);
        when(userService.isCurrentUserOrAdmin(currentUserMock.getId())).thenReturn(true);
        //when & then
        Assertions.assertThat(sut.signUpStudent(course.getId(), currentUserMock.getId())).isTrue();
    }

    @Test
    void should_throw_exception_when_student_already_enrolled() {
        //given
        Student currentUserMock = TestData.getStudent();
        currentUserMock.setId(15L);
        Course course = TestData.getCourse(TestData.getTeacher(), currentUserMock);
        when(courseRepository.getById(course.getId())).thenReturn(java.util.Optional.of(course));
        when(userService.isCurrentUserOrAdmin(currentUserMock.getId())).thenReturn(true);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.signUpStudent(course.getId(), currentUserMock.getId())).isInstanceOf(StudentAlreadyEnrolled.class);
    }

    @Test
    void should_accept_student() {
        //given
        Teacher currentUserMock = TestData.getTeacher();
        Student student = TestData.getStudent();
        currentUserMock.setId(69L);
        Course course = TestData.getCourse(currentUserMock, student);
        when(courseRepository.getById(course.getId())).thenReturn(java.util.Optional.of(course));
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        //when
        sut.acceptStudent(course.getId(), student.getId());
        //then
        Assertions.assertThat(course.getStudents()).allMatch(CourseStudent::isAccepted);
    }

    @Test
    void should_throw_authorization_exception_on_accept_student() {
        //given
        Teacher currentUserMock = TestData.getTeacher();
        Student student = TestData.getStudent();
        currentUserMock.setId(69L);
        Course course = TestData.getCourse(TestData.getTeacher(), student);
        when(courseRepository.getById(course.getId())).thenReturn(java.util.Optional.of(course));
        when(userServiceDetails.getCurrentUser()).thenReturn(currentUserMock);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.acceptStudent(course.getId(), student.getId())).isInstanceOf(AuthorizationException.class);
    }
}