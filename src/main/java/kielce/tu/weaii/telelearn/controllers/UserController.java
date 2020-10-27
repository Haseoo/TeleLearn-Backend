package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.services.adapters.StudentServiceImpl;
import kielce.tu.weaii.telelearn.services.adapters.TeacherServiceImpl;
import kielce.tu.weaii.telelearn.services.adapters.UserServiceImpl;
import kielce.tu.weaii.telelearn.views.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;
    private final StudentServiceImpl studentService;
    private final TeacherServiceImpl teacherService;

    @PostMapping(path = "/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(UserLoginResponse.of(userService.getJwt(loginRequest), userService.getUserByLoginOrEmail(loginRequest.getUserName())), OK);
    }

    @PostMapping(path = "/register/student")
    public ResponseEntity<Object> registerStudent(@RequestBody @Valid StudentRegisterRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/user/student/{id}")
                .buildAndExpand(studentService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/register/teacher")
    public ResponseEntity<Object> resisterTeacher(@RequestBody @Valid TeacherRegisterRequest request) {
        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/user/teacher/{id}")
                .buildAndExpand(teacherService.add(request).getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
