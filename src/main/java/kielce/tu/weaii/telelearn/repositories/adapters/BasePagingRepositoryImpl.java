package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.repositories.ports.BasePagingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class BasePagingRepositoryImpl<E> implements BasePagingRepository<E> {
    protected final PagingAndSortingRepository<E, Long> repository;

    @Override
    public Iterable<E> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<E> getPage(int pageSize, int pageNo, String sortBy) {
        return repository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }

    @Override
    public Page<E> getPage(int pageSize, int pageNo) {
        return repository.findAll(PageRequest.of(pageNo, pageSize));
    }

    @Override
    public Optional<E> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public E save(E entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(E entity) {
        repository.delete(entity);
    }
}
