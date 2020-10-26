package kielce.tu.weaii.telelearn.repositories.ports;

import org.springframework.data.domain.Page;

public interface BasePagingRepository<E> extends BaseRepository<E> {
    Iterable<E> getAll();
    Page<E> getPage(int pageSize, int pageNo, String sortBy);
    Page<E> getPage(int pageSize, int pageNo);
}
