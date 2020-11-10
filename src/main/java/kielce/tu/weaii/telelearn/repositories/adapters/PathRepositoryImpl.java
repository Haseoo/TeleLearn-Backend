package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.courses.Path;
import kielce.tu.weaii.telelearn.repositories.jpa.PathJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.PathRepository;

public class PathRepositoryImpl extends BaseCRUDRepositoryImpl<Path> implements PathRepository {
    private final PathJPARepository jpaRepository;

    public PathRepositoryImpl(PathJPARepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }
}
