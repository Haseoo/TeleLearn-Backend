package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.repositories.jpa.GlobalNewsJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.GlobalNewsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class GlobalNewsRepositoryImpl extends BasePagingRepositoryImpl<GlobalNews> implements GlobalNewsRepository {
    private final GlobalNewsJPARepository repository;

    public GlobalNewsRepositoryImpl(GlobalNewsJPARepository repository) {
        super(repository);
        this.repository = repository;
    }
}
