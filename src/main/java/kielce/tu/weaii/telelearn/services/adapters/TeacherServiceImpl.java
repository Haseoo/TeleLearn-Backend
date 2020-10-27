package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.exceptions.users.UserNotFoundException;
import kielce.tu.weaii.telelearn.models.Teacher;
import kielce.tu.weaii.telelearn.models.UserRole;
import kielce.tu.weaii.telelearn.repositories.ports.TeacherRepository;
import kielce.tu.weaii.telelearn.requests.TeacherRegisterRequest;
import kielce.tu.weaii.telelearn.requests.TeacherUpdateRequest;
import kielce.tu.weaii.telelearn.services.ports.TeacherService;
import kielce.tu.weaii.telelearn.services.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {
    private final UserService userService;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public Teacher getById(Long id) {
        return teacherRepository.getById(id).orElseThrow(() -> new UserNotFoundException(id, UserRole.TEACHER));
    }

    @Transactional
    public Teacher add(TeacherRegisterRequest request) {
        userService.checkAvailability(request.getUsername(), request.getEmail());
        Teacher model = modelMapper.map(request, Teacher.class);
        model.setPassword(passwordEncoder.encode(String.valueOf(model.getPassword())).toCharArray());
        model.setUserRole(UserRole.TEACHER);
        model.setEnabled(true);
        return teacherRepository.save(model);
    }

    @Transactional
    public Teacher update(Long id, TeacherUpdateRequest request) {
        Teacher teacher = getById(id);
        if (!teacher.getEmail().equals(request.getEmail())) {
            userService.checkAvailability(request.getEmail());
        }
        BeanUtils.copyProperties(request, teacher);
        return teacherRepository.save(teacher);
    }

    @Transactional
    public void delete(Long id) {
        teacherRepository.delete(getById(id));
    }

}
