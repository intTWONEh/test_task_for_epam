package com.test.todolist.repositories;

import com.test.todolist.entities.Task;
import com.test.todolist.entities.TaskLinks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskLinksRepository extends CrudRepository<TaskLinks, Long> {
    @Query("SELECT child.id FROM TaskLinks")
    List<Long> listChild();

    @Query("SELECT parent.id FROM TaskLinks")
    List<Long> listParent();

    @Query("SELECT child.id FROM TaskLinks WHERE parent = ?1")
    List<Long> getAllByChild(Task task);

    @Query("SELECT parent.id FROM TaskLinks WHERE child = ?1")
    List<Long> getAllByParent(Task task);
}