package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.CourseNotFoundException;
import kielce.tu.weaii.telelearn.exceptions.courses.StudentOnListNotFound;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.CourseStudent;
import kielce.tu.weaii.telelearn.repositories.ports.CourseRepository;
import kielce.tu.weaii.telelearn.requests.courses.CourseRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.TeacherService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;
    private final UserServiceDetailsImpl userServiceDetails;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final UserService userService;

    @Override
    public Course getById(Long id) {
        Course course = getCourse(id);
        User currentUser = userServiceDetails.getCurrentUser();
        if (!userService.isCurrentUserOrAdmin(course.getOwner().getId()) &&
                course.getStudents()
                        .stream()
                        .filter(CourseStudent::isAccepted)
                        .noneMatch(entry -> entry.getStudent().getId().equals(currentUser.getId()))) {
            throw new AuthorizationException("kurs", currentUser.getId(), id);
        }
        return course;
    }

    @Override
    @Transactional
    public Course add(CourseRequest request) {
        checkAuthorization(request);
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setOwner(teacherService.getById(request.getOwnerId()));
        return repository.save(course);
    }

    @Override
    @Transactional
    public Course update(Long id, CourseRequest request) {
        checkAuthorization(request);
        Course course = getById(id);
        BeanUtils.copyProperties(request, course);
        checkCourseAuthorization(id, course.getOwner().getId());
        if (!request.getOwnerId().equals(course.getOwner().getId())) {
            course.setOwner(teacherService.getById(request.getOwnerId()));
        }
        return course;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course course = getById(id);
        checkCourseAuthorization(id, course.getOwner().getId());
        repository.delete(course);
    }

    @Override
    @Transactional
    public boolean signUpStudent(Long courseId, Long studentId) {
        checkUserAuthorization(studentId);
        CourseStudent courseStudent = prepareStudentOnCourseEntry(courseId, studentId);
        return courseStudent.isAccepted();
    }

    @Override
    @Transactional
    public void acceptStudent(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        checkCourseAuthorization(courseId, course.getOwner().getId());
        CourseStudent courseStudentEntry = getCourseStudentEntry(courseId, studentId, course);
        courseStudentEntry.setAccepted(true);
        repository.save(course);
    }

    @Override
    @Transactional
    public void signOutStudent(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        checkDeletingUserAuthorization(courseId, studentId, course);
        CourseStudent courseStudentEntry = getCourseStudentEntry(courseId, studentId, course);
        course.getStudents().remove(courseStudentEntry);
        repository.save(course);

    }

    private void checkAuthorization(CourseRequest request) {
        if (!userService.isCurrentUserOrAdmin(request.getOwnerId())) {
            throw new AuthorizationException("dodawanie/edycja kursu",
                    userServiceDetails.getCurrentUser().getId(),
                    request.getOwnerId());
        }
    }

    private void checkCourseAuthorization(Long courseId, Long ownerId) {
        User currentUser = userServiceDetails.getCurrentUser();
        if (!currentUser.getUserRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(ownerId)) {
            throw new AuthorizationException("kurs", currentUser.getId(), courseId);
        }
    }

    private void checkUserAuthorization(Long studentId) {
        User currentUser = userServiceDetails.getCurrentUser();
        if (!userService.isCurrentUserOrAdmin(studentId)) {
            throw new AuthorizationException("zapisywanie użytkownika", currentUser.getId(), studentId);
        }
    }

    private CourseStudent prepareStudentOnCourseEntry(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setStudent(studentService.getById(studentId));
        courseStudent.setCourse(course);
        courseStudent.setAccepted(course.isAutoAccept());
        course.getStudents().add(courseStudent);
        return courseStudent;
    }

    private CourseStudent getCourseStudentEntry(Long courseId, Long studentId, Course course) {
        return course.getStudents().stream()
                .filter(entry -> entry.getStudent().getId().equals(studentId))
                .findAny()
                .orElseThrow(() -> new StudentOnListNotFound(courseId, studentId));
    }

    private void checkDeletingUserAuthorization(Long courseId, Long studentId, Course course) {
        User currentUser = userServiceDetails.getCurrentUser();
        if (!currentUser.getUserRole().equals(UserRole.ADMIN) &&
                !currentUser.getId().equals(course.getOwner().getId()) &&
                !currentUser.getId().equals(studentId)) {
            throw new AuthorizationException("usuwanie użytkownika z kursu", currentUser.getId(), courseId);
        }
    }

    private Course getCourse(Long id) {
        return repository.getById(id).orElseThrow(() -> new CourseNotFoundException(id));
    }
}
