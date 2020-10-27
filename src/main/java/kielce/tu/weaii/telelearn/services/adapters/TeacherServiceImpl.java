package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class TeacherServiceImpl {
    private final UserServiceImpl userService;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Teacher add(TeacherRegisterRequest request) {
        userService.checkAvailability(request.getUsername(), request.getEmail());
        Teacher model = modelMapper.map(request, Teacher.class);
        model.setPassword(passwordEncoder.encode(String.valueOf(model.getPassword())).toCharArray());
        model.setUserRole(UserRole.TEACHER);
        model.setEnabled(true);
        return teacherRepository.save(model);
    }

}
