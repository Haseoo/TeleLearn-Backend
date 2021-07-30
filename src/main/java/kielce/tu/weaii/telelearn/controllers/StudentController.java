package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.*;
import kielce.tu.weaii.telelearn.views.StudentView;
import kielce.tu.weaii.telelearn.views.courses.CourseView;
import kielce.tu.weaii.telelearn.views.courses.TaskScheduleView;
import kielce.tu.weaii.telelearn.views.courses.TaskToScheduleRecordView;
import kielce.tu.weaii.telelearn.views.courses.TasksToScheduleView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/student")
public class StudentController {
    private final StudentService studentService;
    private final UserService userService;
    private final TaskService taskService;
    private final TaskScheduleService taskScheduleService;
    private final StudentStatJsonCreator studentStatJsonCreator;

    @GetMapping(path = "/{id}")
    public ResponseEntity<StudentView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(StudentView.from(studentService.getById(id),
                userService.isCurrentUserOrAdmin(id)), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/stat", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<String> getStudentStat(@PathVariable Long id) {
        return new ResponseEntity<>(studentStatJsonCreator.getStudentStatJson(id, LocalDate.now()), HttpStatus.OK);
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
    public ResponseEntity<Object> deleteStudent(@PathVariable Long id) {
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

    @GetMapping(path = "/{id}/tasks")
    public ResponseEntity<Map<String, List<TaskToScheduleRecordView>>> getStudentTasks(@PathVariable Long id) {
        return new ResponseEntity<>(TasksToScheduleView.from(taskService.getStudentByTasksFromCurse(id, LocalDate.now()), id), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/schedule")
    public ResponseEntity<Map<String, List<TaskScheduleView>>> getSchedule(@PathVariable Long id) {
        return new ResponseEntity<>(TaskScheduleView.form(taskScheduleService.getListForStudent(id)), HttpStatus.OK);
    }

}
