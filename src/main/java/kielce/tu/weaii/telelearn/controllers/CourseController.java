package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.courses.CourseRequest;
import kielce.tu.weaii.telelearn.requests.courses.CourseStudentRequest;
import kielce.tu.weaii.telelearn.services.ports.CourseService;
import kielce.tu.weaii.telelearn.services.ports.PathService;
import kielce.tu.weaii.telelearn.services.ports.PostService;
import kielce.tu.weaii.telelearn.views.courses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;
    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(CourseView.from(courseService.getById(id)), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> add(@Valid @RequestBody CourseRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(courseService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        courseService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{courseId}/sign-up")
    public ResponseEntity<CourseSignUpResponse> signUpStudent(@PathVariable Long courseId, @Valid @RequestBody CourseStudentRequest request) {
        return new ResponseEntity<>(new CourseSignUpResponse(courseService.signUpStudent(courseId, request.getStudentId())), HttpStatus.OK);
    }

    @PutMapping(path = "/{courseId}/accept-student")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Object> acceptStudent(@PathVariable Long courseId, @Valid @RequestBody CourseStudentRequest request) {
        courseService.acceptStudent(courseId, request.getStudentId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{courseId}/sign-out")
    public ResponseEntity<Object> signOutStudent(@PathVariable Long courseId, @Valid @RequestBody CourseStudentRequest request) {
        courseService.signOutStudent(courseId, request.getStudentId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}/brief")
    public ResponseEntity<CourseBriefView> getBriefById(@PathVariable Long id) {
        return new ResponseEntity<>(CourseBriefView.from(courseService.getCourse(id)), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/post")
    public ResponseEntity<List<PostView>> getCoursePosts(@PathVariable Long id) {
        return new ResponseEntity<>(postService.getCoursePosts(id).stream()
                .map(PostView::from)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/task")
    public ResponseEntity<List<TaskView>> getCourseTasks(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getById(id).getTasks().stream()
                .map(TaskView::from)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
