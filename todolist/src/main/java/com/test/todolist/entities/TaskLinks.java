package com.test.todolist.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class TaskLinks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Task parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Task child;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TaskLinks taskLinks = (TaskLinks) o;
        return id != null && Objects.equals(id, taskLinks.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
