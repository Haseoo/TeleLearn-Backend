package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.courses.Task;
import kielce.tu.weaii.telelearn.repositories.jpa.TaskJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.TaskRepository;

public class TaskRepositoryImpl extends BaseCRUDRepositoryImpl<Task> implements TaskRepository {
    private final TaskJPARepository jpaRepository;

    public TaskRepositoryImpl(TaskJPARepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }
}
