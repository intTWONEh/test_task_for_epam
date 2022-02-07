package com.test.todolist.services;

import com.test.todolist.entities.Users;
import com.test.todolist.repositories.UserRepository;
import com.test.todolist.services.additionalfunc.PasswordHash;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class UserService {
    private final UserRepository userRepository;
    private String Login;
    private String Password;
    private Optional<Users> User;
    private final Scanner Input = new Scanner(System.in);
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(){
        System.out.println("Регистрация нового пользователя.");

        do {
           try {
                System.out.println("Введите логин:");
                Login = Input.nextLine();
                System.out.println("Введите пароль:");
                Password = PasswordHash.getHash(Input.nextLine()).toLowerCase();
            } catch (Exception e) {
                System.out.println("Ошибка при вводе данных для регистрации!");
                continue;
            }

            User = userRepository.findByLogin(Login);

            if (User.isPresent()) {
                System.out.println("Данный пользователь уже зарегестрирован!");
            } else {
                Users user = new Users();
                user.setLogin(Login);
                user.setPassword(Password);
                userRepository.save(user);
            }
            break;
        } while (true);
    }

    public boolean deleteUser(Long id){
        System.out.println("Удаление пользователя.");
        System.out.println("Введите Yes если хотите удалить пользователя или No для отмены:");
        String action;
        action = Input.next().toLowerCase();

        if(action.equals("yes")) {
                userRepository.deleteById(id);
                //taskRepository.deleteByUserId(id);
                System.out.println("Пользователь удален");
                return true;
        }
        return false;
    }

    //•  при выполнении любой операции с ToDo, будучи неавторизованным пользователем, возвращать соответствующее сообщение.
    public Long authorizationUser(){
        int authCounter = 0;
        System.out.println("Пройдите авторизацию.");

        do {
            try {
                System.out.println("Введите логин:");
                Login = Input.nextLine();
                System.out.println("Введите пароль:");
                Password = PasswordHash.getHash(Input.nextLine()).toLowerCase();
            } catch (Exception e) {
                System.out.println("Ошибка при вводе логина или пароля!");
                continue;
            }

            User = userRepository.findByLoginAndPassword(Login, Password);

            if(User.isEmpty()) {
                System.out.println("Неверный Логин или Пароль! Осталось попыток " + (5 - ++authCounter));
                if(authCounter == 5 ) {
                    System.out.println("Превышен лимит аутентификации!");
                    return 0L;
                }
            } else {
                System.out.println("Пользователь авторизован.");
                return User.get().getId();
            }
        } while (true);
    }

    public Users getUsers(Long id){
        return userRepository.findById(id).get();
    }
}
