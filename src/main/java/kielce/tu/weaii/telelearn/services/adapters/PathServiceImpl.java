package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.courses.PathNotFound;
import kielce.tu.weaii.telelearn.models.User;
import kielce.tu.weaii.telelearn.models.courses.Course;
import kielce.tu.weaii.telelearn.models.courses.Path;
import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.repositories.ports.PathRepository;
import kielce.tu.weaii.telelearn.requests.courses.PathRequest;
import kielce.tu.weaii.telelearn.security.UserServiceDetailsImpl;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.PathService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PathServiceImpl implements PathService {
    private final PathRepository pathRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final UserServiceDetailsImpl userServiceDetails;

    @Override
    public List<Path> getCoursePaths(Long courseId) {
        return courseService.getById(courseId).getPaths();
    }

    @Override
    public Path getById(Long id) {
        Path path = pathRepository.getById(id).orElseThrow(() -> new PathNotFound(id));
        User currentUser = userServiceDetails.getCurrentUser();
        if (!userService.isCurrentUserOrAdmin(path.getCourse().getOwner().getId()) &&
                path.getCourse().getStudents().stream().noneMatch(entry -> entry.getStudent().getId().equals(currentUser.getId()))) {
            throw new AuthorizationException("sieżka", currentUser.getId(), id);
        }
        return path;
    }

    @Override
    @Transactional
    public Path add(PathRequest request) {
        Course course = courseService.getById(request.getCourseId());
        Path path = new Path();
        path.setCourse(course);
        path.setName(request.getName());
        path.setDescription(request.getDescription());
        return pathRepository.save(path);
    }

    @Override
    @Transactional
    public Path update(Long id, PathRequest request) {
        Path path = getById(id);
        path.setName(request.getName());
        path.setDescription(request.getDescription());
        if (!path.getCourse().getId().equals(request.getCourseId())) {
            Course course = courseService.getById(request.getCourseId());
            path.setCourse(course);
        }
        return pathRepository.save(path);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pathRepository.delete(getById(id));
    }

}
