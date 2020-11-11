package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.courses.Path;
import kielce.tu.weaii.telelearn.requests.courses.PathRequest;

import java.util.List;

public interface PathService {
    List<Path> getCoursePaths(Long courseId);

    Path getById(Long id);

    Path add(PathRequest request);

    Path update(Long id, PathRequest request);

    void delete(Long id);
}
