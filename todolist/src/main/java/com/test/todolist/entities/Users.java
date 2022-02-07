package com.test.todolist.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String login;

    @Column(columnDefinition = "varchar(33)", nullable = false)
    private String password;

    @OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    private List<Task> task = new LinkedList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Users users = (Users) o;
        return id != null && Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}