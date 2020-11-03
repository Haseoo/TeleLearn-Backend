package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import kielce.tu.weaii.telelearn.views.StudentView;
import kielce.tu.weaii.telelearn.views.courses.CourseView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/student")
public class StudentController {
    public final StudentService studentService;
    public final UserService userService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<StudentView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(StudentView.from(studentService.getById(id),
                userService.isCurrentUserOrAdmin(id)), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid StudentUpdateRequest request) {
        studentService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> resisterStudent(@RequestBody @Valid StudentRegisterRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/user/student/{id}")
                .buildAndExpand(studentService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteTeacher(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{userId}/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<CourseView>> getCourses(@PathVariable Long userId) {
        return new ResponseEntity<>(studentService.getCourses(userId).stream()
                .map(CourseView::from)
                .collect(toList()), HttpStatus.OK);
    }

}
