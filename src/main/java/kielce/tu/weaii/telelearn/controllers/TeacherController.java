package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.TeacherService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import kielce.tu.weaii.telelearn.views.TeacherView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/teacher")
public class TeacherController {
    public final TeacherService teacherService;
    public final UserService userService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TeacherView> getById(@PathVariable Long id) {
        return new ResponseEntity<>(TeacherView.from(teacherService.getById(id),
                userService.isCurrentUserOrAdmin(id)), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid TeacherUpdateRequest request) {
        teacherService.update(id, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public ResponseEntity<Object> resisterTeacher(@RequestBody @Valid TeacherRegisterRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/user/teacher/{id}")
                .buildAndExpand(teacherService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteTeacher(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}