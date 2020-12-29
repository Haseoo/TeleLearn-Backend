package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.Constants;
import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.PostCommentingNotAllowed;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.courses.Comment;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.models.courses.PostVisibility;
import kielce.tu.weaii.telelearn.repositories.jpa.CommentJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.PostRepository;
import kielce.tu.weaii.telelearn.requests.courses.PostCommentRequest;
import kielce.tu.weaii.telelearn.requests.courses.PostRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@Tag(Constants.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CourseService courseService;
    @Mock
    private UserServiceDetailsImpl userServiceDetails;
    @Mock
    private CommentJPARepository commentJpaRepository;

    @InjectMocks
    private PostServiceImpl sut;

    private Teacher teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void beforeEach() {
        teacher = TestData.getTeacher();
        student = TestData.getStudent();
        course = TestData.getCourse(teacher, student);
    }

    @Test
    void should_throw_not_found_exception_when_course_doesnt_exist() {
        //given
        final long id = 1L;
        when(postRepository.getById(id)).thenReturn(Optional.empty());
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_authorization_exception_when_teacher_is_not_course_owner() {
        //given
        Post post = TestData.getPost(course, student);
        Teacher otherTeacher = TestData.getTeacher();
        otherTeacher.setId(15L);
        final Long id = post.getId();
        when(postRepository.getById(id)).thenReturn(Optional.of(post));
        when(userServiceDetails.getCurrentUser()).thenReturn(otherTeacher);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_authorization_exception_when_post_is_not_for_student() {
        //given
        Post post = TestData.getPost(course, teacher);
        post.setPostVisibility(PostVisibility.ONLY_TEACHER);
        final Long id = post.getId();
        when(postRepository.getById(id)).thenReturn(Optional.of(post));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_for_and_return_post_by_id() {
        //given
        Post post = TestData.getPost(course, student);
        post.setPostVisibility(PostVisibility.ONLY_TEACHER);
        final Long id = post.getId();
        when(postRepository.getById(id)).thenReturn(Optional.of(post));
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when
        Post out = sut.getById(id);
        //then
        Assertions.assertThat(out).isEqualTo(post);
        verify(postRepository).getById(id);
    }

    @Test
    void should_return_all_posts_if_current_user_is_teacher() {
        //given
        Post post1 = TestData.getPost(course, student);
        Post post2 = TestData.getPost(course, student);
        course.getPosts().add(post1);
        course.getPosts().add(post2);
        post2.setPostVisibility(PostVisibility.ONLY_TEACHER);
        final Long id = course.getId();
        when(courseService.getById(id)).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when & then
        Assertions.assertThat(sut.getCoursePosts(id)).contains(post1, post2);
    }

    @Test
    void should_return_only_post_for_everyone_if_current_user_is_student() {
        //given
        Student otherStudent = TestData.getStudent();
        otherStudent.setId(15L);
        Post post1 = TestData.getPost(course, student);
        Post post2 = TestData.getPost(course, otherStudent);
        course.getPosts().add(post1);
        course.getPosts().add(post2);
        post2.setPostVisibility(PostVisibility.ONLY_TEACHER);
        final Long id = course.getId();
        when(courseService.getById(id)).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThat(sut.getCoursePosts(id)).contains(post1).doesNotContain(post2);
    }

    @Test
    void should_throw_authorization_exception_when_students_are_not_allowed_to_post() {
        //given
        course.setStudentsAllowedToPost(false);
        when(courseService.getById(any())).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        //when & then
        Assertions.assertThatThrownBy(() -> sut.addPost(TestData.getPostRequest(course.getId()), new ArrayList<>())).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_to_add_post() throws IOException {
        //given
        when(courseService.getById(any())).thenReturn(course);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        PostRequest request = TestData.getPostRequest(course.getId());
        MultipartFile attachment = TestData.getMultipartFile();
        ArgumentCaptor<Post> entityToAddCaptor = ArgumentCaptor.forClass(Post.class);
        //when
        sut.addPost(request, Arrays.asList(attachment));
        //then
        verify(courseService).getById(course.getId());
        verify(postRepository).save(entityToAddCaptor.capture());
        Post entityToAdd = entityToAddCaptor.getValue();
        Assertions.assertThat(entityToAdd.getCourse()).isEqualTo(course);
        Assertions.assertThat(entityToAdd.getAuthor()).isEqualTo(student);
        Assertions.assertThat(entityToAdd.getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(entityToAdd.getPostVisibility()).isEqualTo(request.getPostVisibility());
        Assertions.assertThat(entityToAdd.isCommentingAllowed()).isEqualTo(request.isCommentingAllowed());

        Attachment attachmentToAdd = entityToAdd.getAttachments().get(0);
        Assertions.assertThat(attachmentToAdd.getPost()).isEqualTo(entityToAdd);
        Assertions.assertThat(attachmentToAdd.getFileType()).isEqualTo(attachment.getContentType());
        Assertions.assertThat(attachmentToAdd.getFileName()).isEqualTo(attachment.getOriginalFilename());
        Assertions.assertThat(attachmentToAdd.getAttachmentData().get(0).getData()).isEqualTo(attachment.getBytes());
        Assertions.assertThat(attachmentToAdd.getTask()).isNull();
    }

    @Test
    void should_ask_to_update_post() throws IOException {
        //given
        final Long id = 1L;
        course.setId(5L);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        Post post = TestData.getPost(course, student);
        Attachment attachmentToDelete = TestData.getAttachment();
        attachmentToDelete.setId(5L);
        post.getAttachments().add(attachmentToDelete);
        when(postRepository.getById(id)).thenReturn(Optional.of(post));
        PostRequest request = TestData.getPostRequest(course.getId());
        MultipartFile attachment = TestData.getMultipartFile();
        request.getAttachmentIdsToDelete().add(attachmentToDelete.getId());
        ArgumentCaptor<Post> entityToAddCaptor = ArgumentCaptor.forClass(Post.class);
        //when
        sut.updatePost(id, request, Arrays.asList(attachment));
        //then
        verify(postRepository).getById(id);
        verify(postRepository).save(entityToAddCaptor.capture());
        Post entityToAdd = entityToAddCaptor.getValue();
        Assertions.assertThat(entityToAdd.getCourse()).isEqualTo(course);
        Assertions.assertThat(entityToAdd.getAuthor()).isEqualTo(student);
        Assertions.assertThat(entityToAdd.getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(entityToAdd.getPostVisibility()).isEqualTo(request.getPostVisibility());
        Assertions.assertThat(entityToAdd.isCommentingAllowed()).isEqualTo(request.isCommentingAllowed());
        Assertions.assertThat(entityToAdd.getAttachments()).doesNotContain(attachmentToDelete);

        Attachment attachmentToAdd = entityToAdd.getAttachments().get(0);
        Assertions.assertThat(attachmentToAdd.getPost()).isEqualTo(entityToAdd);
        Assertions.assertThat(attachmentToAdd.getFileType()).isEqualTo(attachment.getContentType());
        Assertions.assertThat(attachmentToAdd.getFileName()).isEqualTo(attachment.getOriginalFilename());
        Assertions.assertThat(attachmentToAdd.getAttachmentData().get(0).getData()).isEqualTo(attachment.getBytes());
        Assertions.assertThat(attachmentToAdd.getTask()).isNull();
    }

    @Test
    void should_ask_to_throw_authorization_exception_when_author_is_not_current_user_on_update() {
        //given
        final Long id = 1L;
        course.setId(5L);
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        when(postRepository.getById(id)).thenReturn(Optional.of(TestData.getPost(course, student)));
        PostRequest request = TestData.getPostRequest(course.getId());
        MultipartFile attachment = TestData.getMultipartFile();
        //when & then
        Assertions.assertThatThrownBy(() -> sut.updatePost(id, request, Arrays.asList(attachment))).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_return_post_comments() {
        //given
        Post post = TestData.getPost(course, student);
        when(postRepository.getById(post.getId())).thenReturn(Optional.of(TestData.getPost(course, student)));
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        //when & then
        Assertions.assertThat(sut.getComments(post.getId())).hasSameSizeAs(post.getComments());
    }

    @Test
    void should_add_comment() {
        //given
        Post post = TestData.getPost(course, student);
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        PostCommentRequest request = new PostCommentRequest("content");
        when(postRepository.getById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenReturn(post);
        //when & then
        Assertions.assertThat(sut.addComment(post.getId(), request))
                .anyMatch(c -> c.getContent().equals(request.getContent()) &&
                        c.getAuthor().equals(teacher));
    }

    @Test
    void should_throw_post_comment_not_allowed_exception() {
        //given
        Post post = TestData.getPost(course, student);
        post.setCommentingAllowed(false);
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        when(postRepository.getById(post.getId())).thenReturn(Optional.of(post));
        PostCommentRequest request = new PostCommentRequest("content");
        //when & then
        Assertions.assertThatThrownBy(() -> sut.addComment(post.getId(), request)).isInstanceOf(PostCommentingNotAllowed.class);
    }

    @Test
    void should_ask_to_delete_post() {
        //given
        Post post = TestData.getPost(course, student);
        post.setCommentingAllowed(false);
        when(userServiceDetails.getCurrentUser()).thenReturn(teacher);
        when(postRepository.getById(post.getId())).thenReturn(Optional.of(post));
        //when
        sut.removePost(post.getId());
        //then
        verify(postRepository).delete(post);
    }

    @Test
    void should_throw_authorization_exception_when_student_delete_not_owned_post() {
        //given
        Post post = TestData.getPost(course, teacher);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        when(postRepository.getById(post.getId())).thenReturn(Optional.of(post));
        //when & then
        Assertions.assertThatThrownBy(() -> sut.removePost(post.getId())).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_ask_to_delete_comment() {
        //given
        Post post = TestData.getPost(course, teacher);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        Comment comment = post.getComments().get(0);
        comment.setAuthor(student);
        when(commentJpaRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        //when
        sut.deleteComment(comment.getId());
        //then
        verify(commentJpaRepository).delete(comment);
    }

    @Test
    void should_throw_authorization_exception_when_delete_not_owned_comment() {
        //given
        Post post = TestData.getPost(course, teacher);
        when(userServiceDetails.getCurrentUser()).thenReturn(student);
        Comment comment = post.getComments().get(0);
        comment.setAuthor(teacher);
        when(commentJpaRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        //when
        Assertions.assertThatThrownBy(() -> sut.deleteComment(comment.getId())).isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_throw_not_found_exception_when_delete_not_existent_comment() {
        //given
        final Long id = 1L;
        Post post = TestData.getPost(course, teacher);
        when(commentJpaRepository.findById(id)).thenReturn(Optional.empty());
        //when
        Assertions.assertThatThrownBy(() -> sut.deleteComment(id)).isInstanceOf(NotFoundException.class);
    }

}