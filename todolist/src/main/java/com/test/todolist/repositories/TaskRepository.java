package com.test.todolist.repositories;

import com.test.todolist.entities.Task;
import com.test.todolist.entities.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByDescriptionAndUsers(String description, Users user);

    @Query("SELECT taskEndTime FROM Task WHERE messageSend = false and status = false")
    List<LocalDateTime> getTime();

    @Query("SELECT id FROM Task WHERE taskEndTime = ?1")
    List<Long> findTaskTimeEnd(LocalDateTime timeEnd);

    @Query("select description from Task where id = ?1")
    String findDescription(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Task SET messageSend = true where id = ?1")
    void setSendMessage(Long id);

    @Query("select t from Task t where t.status = false and t.users = ?1")
    List<Task> getOutstandingTask(Users users);

    @Query("select t from Task t where t.status = true and t.users = ?1")
    List<Task> getCompletedTasks(Users users);
}