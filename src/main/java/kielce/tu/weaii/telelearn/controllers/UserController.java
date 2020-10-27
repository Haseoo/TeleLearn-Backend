package kielce.tu.weaii.telelearn.controllers;

import kielce.tu.weaii.telelearn.requests.LoginRequest;
import kielce.tu.weaii.telelearn.services.adapters.UserServiceImpl;
import kielce.tu.weaii.telelearn.views.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping(path = "/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(UserLoginResponse.of(userService.getJwt(loginRequest), userService.getUserByLoginOrEmail(loginRequest.getUserName())), OK);
    }
}
