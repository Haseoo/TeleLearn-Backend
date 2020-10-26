package kielce.tu.weaii.telelearn.repositories.ports;

import java.util.List;

public interface BaseCRUDRepository<E> extends BaseRepository<E> {
    List<E> getAll();
}
