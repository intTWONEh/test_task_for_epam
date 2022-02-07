package com.test.todolist.services;

import com.test.todolist.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

//•  отправка сообщения за 1 час до дедлайна какого-либо из ToDo;
@Component
public class ScheduledSendMessage {
    private final TaskRepository taskRepository;
    private Long tmpTime;
    public ScheduledSendMessage(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Scheduled(fixedRate = 30000)
    void checkMessageAndSendMessage(){

        List<LocalDateTime> listTimeTask = taskRepository.getTime();

        listTimeTask.forEach(
                (timeEnd) -> {
                    tmpTime = timeToTaskCompletion(timeEnd, LocalDateTime.now());
                    if(tmpTime <= 60 && tmpTime > 0) {
                        System.out.println("\nОстался час до дедлайна!!!!!!");
                        taskRepository.findTaskTimeEnd(timeEnd)
                                .forEach((id) -> {
                                        System.out.println(taskRepository.findDescription(id));
                                        taskRepository.setSendMessage(id);
                                        }
                                );
                    }
                    if(tmpTime < 0){
                        System.out.println("Задача просрочена!!!");
                        taskRepository.findTaskTimeEnd(timeEnd)
                                .forEach((id) -> {
                                            System.out.println(taskRepository.findDescription(id));
                                            taskRepository.setSendMessage(id);
                                        }
                                );
                    }
                }
        );
    }

    private Long timeToTaskCompletion(LocalDateTime Start, LocalDateTime End){
        return ChronoUnit.MINUTES.between(End, Start);
    }
}
