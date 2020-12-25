package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.repositories.ports.AttachmentRepository;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {
    @Mock
    private AttachmentRepository repository;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;

    @InjectMocks
    private AttachmentServiceImpl sut;

    @Test
    void should_return_attachment_for_post_if_teacher_is_course_owner() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Teacher teacher = TestData.getTeacher();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        Post post = TestData.getPost(course, teacher);
        attachment.setPost(post);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when
        Attachment out = sut.getById(id);
        //then
        verify(repository).getById(id);
        Assertions.assertThat(out).isEqualTo(attachment);
    }

    @Test
    void should_return_attachment_for_task_if_teacher_is_course_owner() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Teacher teacher = TestData.getTeacher();
        Course course = TestData.getCourse(teacher, TestData.getStudent());
        Task task = TestData.getTask(course);
        attachment.setTask(task);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when
        Attachment out = sut.getById(id);
        //then
        verify(repository).getById(id);
        Assertions.assertThat(out).isEqualTo(attachment);
    }

    @Test
    void should_throw_authorization_exception_when_teacher_try_access_attachment_of_post_from_not_owned_course() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Teacher teacher = TestData.getTeacher();
        teacher.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        Post post = TestData.getPost(course, TestData.getStudent());
        attachment.setPost(post);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_authorization_exception_when_teacher_try_access_attachment_of_task_from_not_owned_course() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Teacher teacher = TestData.getTeacher();
        teacher.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        Task task = TestData.getTask(course);
        attachment.setTask(task);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_return_attachment_for_post_if_student_is_course_participant() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Student student = TestData.getStudent();
        student.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), student);
        Post post = TestData.getPost(course, TestData.getStudent());
        attachment.setPost(post);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        //when
        Attachment out = sut.getById(id);
        //then
        verify(repository).getById(id);
        Assertions.assertThat(out).isEqualTo(attachment);
    }

    @Test
    void should_return_attachment_for_task_if_student_is_course_participant() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Student student = TestData.getStudent();
        student.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), student);
        Task task = TestData.getTask(course);
        attachment.setTask(task);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        //when
        Attachment out = sut.getById(id);
        //then
        verify(repository).getById(id);
        Assertions.assertThat(out).isEqualTo(attachment);
    }

    @Test
    void should_throw_authorization_exception_when_student_try_access_attachment_of_post_from_course_that_is_not_a_participant() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Student student = TestData.getStudent();
        student.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        Post post = TestData.getPost(course, TestData.getStudent());
        attachment.setPost(post);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_authorization_exception_when_student_try_access_attachment_of_task_from_course_that_is_not_a_participant() {
        //given
        final long id = 1L;
        Attachment attachment = TestData.getAttachment();
        Student student = TestData.getStudent();
        student.setId(5L);
        Course course = TestData.getCourse(TestData.getTeacher(), TestData.getStudent());
        Task task = TestData.getTask(course);
        attachment.setTask(task);
        when(repository.getById(id)).thenReturn(java.util.Optional.of(attachment));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

}