package kielce.tu.weaii.telelearn.repositories.ports;

import java.util.Optional;

public interface BaseRepository<E> {
    Optional<E> getById(Long id);

    E save(E entity);
}
