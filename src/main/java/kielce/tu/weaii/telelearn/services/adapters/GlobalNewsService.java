package kielce.tu.weaii.telelearn.services.adapters;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.repositories.adapters.GlobalNewsRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalNewsService {
    private final GlobalNewsRepositoryImpl repository;
    public Page<GlobalNews> getPage(int pageSize, int pageNo) {
        return repository.getPage(pageSize, pageNo, "publicationDate");
    }

    public GlobalNews add(GlobalNews model) {
        return repository.save(model);
    }
}
