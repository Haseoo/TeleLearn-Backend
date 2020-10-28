package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.models.Student;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.repositories.ports.StudentRepository;
import kielce.tu.weaii.telelearn.requests.StudentRegisterRequest;
import kielce.tu.weaii.telelearn.requests.StudentUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.StudentService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {
    private final UserService userService;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public Student getById(Long id) {
        return studentRepository.getById(id).orElseThrow(() -> new UserNotFoundException(id, UserRole.STUDENT));
    }

    @Transactional
    public Student add(StudentRegisterRequest request) {
        userService.checkAvailability(request.getUsername(), request.getEmail());
        Student model = modelMapper.map(request, Student.class);
        model.setPassword(passwordEncoder.encode(String.valueOf(model.getPassword())).toCharArray());
        model.setUserRole(UserRole.STUDENT);
        model.setEnabled(true);
        return studentRepository.save(model);
    }

    @Transactional
    public Student update(Long id, StudentUpdateRequest request) {
        if (!userService.isCurrentUserOrAdmin(id)) {
            throw new AuthorizationException("użytkownik", null, id);
        }
        Student student = getById(id);
        if (!student.getEmail().equals(request.getEmail())) {
            userService.checkAvailability(request.getEmail());
        }
        BeanUtils.copyProperties(request, student);
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getById(id));
    }

}
