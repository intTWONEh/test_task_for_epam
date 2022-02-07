package com.test.todolist.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@AllArgsConstructor
public class MenuApp {
    private final TaskService taskService;
    private final UserService userService;
    private final Scanner Input = new Scanner(System.in);

    public void startApp(){
        Long userId;
        do{
            userId = MenuAuth();
            if(userId.equals(-1L)) return;
            if(userId > 0L) MenuAction(userId);
        }while (true);
    }

    private Long MenuAuth(){
        do {
            System.out.println("\n[Меню авторизации]");
            System.out.println(
                    "1. Войти." +
                    "\n2. Создать пользователя." +
                    "\n3. Выйти."
            );

            switch (Input.next()) {
                case "1":
                    return userService.authorizationUser();
                case "2":
                    userService.createUser();
                    break;
                case "3":
                    return -1L;
                default: {
                    System.out.println("Не вверный ввод!");
                }
            }
        }while (true);
    }

    private void MenuAction(Long userId){
        String action;

        do{
        System.out.println("\n[Меню действий]");
        System.out.println(
                "1. Создать задачу." +
                "\n2. Удалить задачу." +
                "\n3. Изменить задачу." +
                "\n4. Связать задачи." +
                "\n5. Вывести список невыполненных задач." +
                "\n6. Вывести список выполненных задач." +
                "\n7. Выполнить задачу." +
                "\n8. Удалить пользователя." +
                "\n0. Выйти."
        );

        action = Input.next();

            switch (action){
                case "1": taskService.createTask(userId);break;
                case "2": taskService.deleteTask(userId);break;
                case "3": taskService.updateTask(userId);break;
                case "4": taskService.linkTask(userId);break;
                case "5": taskService.showNotEndTask(userId);break;
                case "6": taskService.showEndTask(userId);break;
                case "7": taskService.checkTask(userId);break;
                case "8": { if(userService.deleteUser(userId)) return; else break;}
                case "0": return;
                default:
                    System.out.println("Не вверный ввод!");
            }
        }while (true);
    }
}
