package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.repositories.ports.StudentRepository;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private StudentRepository studentRepository;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentServiceImpl sut;

    @Test
    void should_ask_for_and_return_student_by_id() {
        //given
        Student student = TestData.getStudent();
        final long id = student.getId();
        when(studentRepository.getById(id)).thenReturn(java.util.Optional.of(student));
        //when
        Student out = sut.getById(id);
        //then
        Assertions.assertThat(out).isEqualTo(student);
        verify(studentRepository).getById(id);
    }

    @Test
    void should_throw_user_not_found_when_student_doest_exits() {
        //given
        final long id = 0L;
        when(studentRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void should_ask_to_add_student() {
        //given
        StudentRegisterRequest request = TestData.getStudentRegisterRequest();
        ArgumentCaptor<Student> modelToSaveRequest = ArgumentCaptor.forClass(Student.class);
        when(passwordEncoder.encode(any())).thenReturn("");
        //when
        sut.add(request);
        //then
        verify(studentRepository).save(modelToSaveRequest.capture());
        Student modelToSave = modelToSaveRequest.getValue();
        Assertions.assertThat(modelToSave.getUserRole()).isEqualTo(UserRole.STUDENT);
        Assertions.assertThat(modelToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(modelToSave.getSurname()).isEqualTo(request.getSurname());
        Assertions.assertThat(modelToSave.getUnit()).isEqualTo(request.getUnit());
        Assertions.assertThat(modelToSave.isEnabled()).isTrue();
    }

    @Test
    void should_update_student_when_current_user_is_admin_or_subject() {
        //given
        Student student = TestData.getStudent();
        Long id = student.getId();
        StudentUpdateRequest request = TestData.getStudentUpdateRequest();
        ArgumentCaptor<Student> modelToSaveRequest = ArgumentCaptor.forClass(Student.class);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        when(studentRepository.getById(id)).thenReturn(Optional.of(student));
        //when
        sut.update(id, request);
        //then
        verify(studentRepository).save(modelToSaveRequest.capture());
        Student modelToSave = modelToSaveRequest.getValue();
        Assertions.assertThat(modelToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(modelToSave.getSurname()).isEqualTo(request.getSurname());
        Assertions.assertThat(modelToSave.getUnit()).isEqualTo(request.getUnit());
    }

    @Test
    void should_throw_authorization_when_current_user_is_not_admin_or_subject() {
        //given
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.update(0L, null)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_delete_user() {
        //given
        Student student = TestData.getStudent();
        final long id = student.getId();
        when(studentRepository.getById(id)).thenReturn(java.util.Optional.of(student));
        //when
        sut.delete(id);
        //then
        verify(studentRepository).delete(student);
    }

    @Test
    void should_return_student_courses_that_participates() {
        //given
        Student student = TestData.getStudent();
        final long id = student.getId();
        Course course1 = TestData.getCourse(TestData.getTeacher(), student);
        course1.setId(1L);
        Course course2 = TestData.getCourse(TestData.getTeacher(), student);
        course2.setId(2L);
        course2.getStudents().get(0).setAccepted(false);
        student.getCourses().addAll(course1.getStudents());
        student.getCourses().addAll(course2.getStudents());
        when(studentRepository.getById(id)).thenReturn(Optional.of(student));
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        //when & then
        Assertions.assertThat(sut.getCourses(id)).contains(course1).doesNotContain(course2);
    }

    @Test
    void should_throw_authorization_exception_when_current_user_is_neither_admin_or_subject() {
        //given
        Student student = TestData.getStudent();
        student.setId(15L);
        final long id = student.getId();
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(false);
        when(userServiceDetails.getCurrentUser()).thenReturn(TestData.getStudent());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getCourses(id)).isInstanceOf(AuthorizationException.class);
    }
}