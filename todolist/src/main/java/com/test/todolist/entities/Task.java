package com.test.todolist.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime taskStartTime;

    @Column(nullable = false)
    private LocalDateTime taskEndTime;

    @Column(columnDefinition = "boolean default false")
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Users users;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<TaskLinks> task_1 = new LinkedList<>();

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    private List<TaskLinks> task_2 = new LinkedList<>();


    @Column(columnDefinition = "boolean default false")
    private Boolean messageSend;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
