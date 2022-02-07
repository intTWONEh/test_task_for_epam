package com.test.todolist.repositories;

import com.test.todolist.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByLoginAndPassword(String Login, String Password);
    Optional<Users> findByLogin(String Login);
}
