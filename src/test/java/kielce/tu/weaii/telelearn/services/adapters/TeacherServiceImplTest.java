package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherUpdateRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private TeacherRepository teacherRepository;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherServiceImpl sut;

    @Test
    void should_ask_for_and_return_list_of_teachers() {
        //given
        List<Teacher> teachers = Arrays.asList(TestData.getTeacher(), TestData.getTeacher());
        when(teacherRepository.getAll()).thenReturn(teachers);
        //when
        List<Teacher> out = sut.getAll();
        //then
        Assertions.assertThat(out).isEqualTo(teachers);
        verify(teacherRepository).getAll();
    }

    @Test
    void should_ask_and_return_teacher_by_id() {
        //given
        Teacher searched = TestData.getTeacher();
        final Long id = searched.getId();
        when(teacherRepository.getById(id)).thenReturn(java.util.Optional.of(searched));
        //when
        Teacher out = sut.getById(id);
        //then
        Assertions.assertThat(out).isEqualTo(searched);
        verify(teacherRepository).getById(id);
    }

    @Test
    void should_throw_user_not_found_exception_when_teacher_doesnt_exist() {
        //given
        final Long id = 0L;
        when(teacherRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void should_ask_to_add_teacher() {
        //given
        TeacherRegisterRequest request = TestData.getTeacherRegisterRequest();
        ArgumentCaptor<Teacher> modelToSaveRequest = ArgumentCaptor.forClass(Teacher.class);
        when(passwordEncoder.encode(any())).thenReturn("");
        //when
        sut.add(request);
        //then
        verify(teacherRepository).save(modelToSaveRequest.capture());
        Teacher modelToSave = modelToSaveRequest.getValue();
        Assertions.assertThat(modelToSave.getUserRole()).isEqualTo(UserRole.TEACHER);
        Assertions.assertThat(modelToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(modelToSave.getSurname()).isEqualTo(request.getSurname());
        Assertions.assertThat(modelToSave.getUnit()).isEqualTo(request.getUnit());
        Assertions.assertThat(modelToSave.getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(modelToSave.isEnabled()).isTrue();
    }

    @Test
    void should_update_teacher() {
        //given
        Teacher teacher = TestData.getTeacher();
        Long id = teacher.getId();
        TeacherUpdateRequest request = TestData.getTeacherUpdateRequest();
        ArgumentCaptor<Teacher> modelToSaveRequest = ArgumentCaptor.forClass(Teacher.class);
        when(userService.isCurrentUserOrAdmin(id)).thenReturn(true);
        when(teacherRepository.getById(id)).thenReturn(Optional.of(teacher));
        //when
        sut.update(id, request);
        //then
        verify(teacherRepository).save(modelToSaveRequest.capture());
        Teacher modelToSave = modelToSaveRequest.getValue();
        Assertions.assertThat(modelToSave.getName()).isEqualTo(request.getName());
        Assertions.assertThat(modelToSave.getSurname()).isEqualTo(request.getSurname());
        Assertions.assertThat(modelToSave.getUnit()).isEqualTo(request.getUnit());
        Assertions.assertThat(modelToSave.getTitle()).isEqualTo(request.getTitle());
    }

    @Test
    void should_trow_authorization_exception_when_current_user_is_not_subject_or_admin_on_update() {
        //given
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.update(0L, null)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_delete_teacher() {
        //given
        Teacher toDelete = TestData.getTeacher();
        final Long id = toDelete.getId();
        when(teacherRepository.getById(id)).thenReturn(java.util.Optional.of(toDelete));
        //when
        sut.delete(id);
        //then
        verify(teacherRepository).delete(toDelete);
    }

    @Test
    void should_return_all_courses_when_teacher_is_requesting() {
        //given
        Teacher teacher = TestData.getTeacher();
        Course publicCourse = TestData.getCourse(teacher, TestData.getStudent());
        Course privateCourse = TestData.getCourse(teacher, TestData.getStudent());
        privateCourse.setPublicCourse(false);
        teacher.getCourses().add(publicCourse);
        teacher.getCourses().add(privateCourse);
        when(userService.isCurrentUserOrAdmin(teacher.getId())).thenReturn(true);
        when(teacherRepository.getById(teacher.getId())).thenReturn(Optional.of(teacher));
        //when
        List<Course> out = sut.getCourses(teacher.getId());
        //then
        Assertions.assertThat(out).contains(publicCourse, privateCourse);
    }

    @Test
    void should_return_only_public_courses_another_user_is_requesting() {
        //given
        Teacher teacher = TestData.getTeacher();
        Course publicCourse = TestData.getCourse(teacher, TestData.getStudent());
        Course privateCourse = TestData.getCourse(teacher, TestData.getStudent());
        privateCourse.setPublicCourse(false);
        teacher.getCourses().add(publicCourse);
        teacher.getCourses().add(privateCourse);
        when(userService.isCurrentUserOrAdmin(any())).thenReturn(false);
        when(teacherRepository.getById(teacher.getId())).thenReturn(Optional.of(teacher));
        //when
        List<Course> out = sut.getCourses(teacher.getId());
        //then
        Assertions.assertThat(out).contains(publicCourse).doesNotContain(privateCourse);
    }
}