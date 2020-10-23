package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.repositories.jpa.GlobalNewsJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GlobalNewsRepository {
    private final GlobalNewsJPARepository jpaRepository;

    public Page<GlobalNews> getPage(int pageSize, int pageNo, String sortBy) {
        return jpaRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }
}
