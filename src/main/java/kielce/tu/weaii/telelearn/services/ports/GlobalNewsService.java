package kielce.tu.weaii.telelearn.services.ports;

import kielce.tu.weaii.telelearn.models.GlobalNews;
import kielce.tu.weaii.telelearn.requests.GlobalNewsRequest;
import org.springframework.data.domain.Page;

public interface GlobalNewsService {
    GlobalNews getById(Long id);
    GlobalNews add(GlobalNewsRequest request);
    GlobalNews edit(Long id, GlobalNewsRequest request);
    public Page<GlobalNews> getPage(int pageSize, int pageNo);
    void delete(Long id);
}
