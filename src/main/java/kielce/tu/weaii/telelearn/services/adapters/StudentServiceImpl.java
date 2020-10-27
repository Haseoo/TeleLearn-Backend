package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.repositories.ports.StudentRepository;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl {
    private final UserServiceImpl userService;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Student add(StudentRegisterRequest request) {
        userService.checkAvailability(request.getUsername(), request.getEmail());
        Student model = modelMapper.map(request, Student.class);
        model.setPassword(passwordEncoder.encode(String.valueOf(model.getPassword())).toCharArray());
        model.setUserRole(UserRole.STUDENT);
        model.setEnabled(true);
        return studentRepository.save(model);
    }

}
