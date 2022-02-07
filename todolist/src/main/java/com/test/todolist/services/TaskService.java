package com.test.todolist.services;

import com.test.todolist.entities.Task;
import com.test.todolist.entities.TaskLinks;
import com.test.todolist.repositories.TaskLinksRepository;
import com.test.todolist.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskLinksRepository taskLinksRepository;
    private final UserService userService;
    private final Scanner Input = new Scanner(System.in);

    public TaskService(TaskRepository taskRepository, TaskLinksRepository taskLinksRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.taskLinksRepository = taskLinksRepository;
        this.userService = userService;
    }

    //•  Создание объекта ToDo;
    public void createTask(Long id){
        System.out.println("Введите свойства новой задачи");
        Task task = new Task();
        task.setDescription(requestDescription());
        task.setTaskStartTime(LocalDateTime.now());
        task.setTaskEndTime(requestDateEnd());
        task.setStatus(false);
        task.setUsers(userService.getUsers(id));
        task.setMessageSend(false);
        taskRepository.save(task);
    }

    //•  создание дочернего объекта ToDo (Уровень вложенности неограничен);
    public void linkTask(Long id){
        Long parentTaskId;
        Long childTaskId;

        do{
            System.out.println("Для выхода напишите Exit, если хотите ещё создать связь то введите любое значение: ");
            if(Input.next().equalsIgnoreCase("exit")) return;

            parentTaskId = getIdTask("Укажите имя задачи для которой нужно сделать связь: ", id);
            childTaskId = getIdTask("Укажите имя задачи которая будет под задачей: ", id);

            if(checkLinkTask(parentTaskId, childTaskId)){
                TaskLinks taskLinks = new TaskLinks();
                taskLinks.setParent(taskRepository.findById(parentTaskId).get());
                taskLinks.setChild(taskRepository.findById(childTaskId).get());
                taskLinksRepository.save(taskLinks);
                System.out.println("Связь создана.");
                return;
            }
        }while (true);
    }

    //•  пометка заданного ToDo как выполненное (Доступно, только если все дочерние ToDo выполнены);
    public void checkTask(Long id){
        Long idTask = getIdTask("Введите название выполненой задачи: ",id);
        Optional<Task> Task = taskRepository.findById(idTask);
        List<Long> listParentId = taskLinksRepository.getAllByParent(Task.get());

        if(Task.get().getStatus()){
            System.out.println("Задача уже выполнена");
            return;
        }

        if(!listParentId.isEmpty()){
            AtomicReference<Boolean> tmpStatus = new AtomicReference<>(true);

            listParentId.forEach(
                    (parentTaskId) -> {
                        Optional<Task> tmpTask = taskRepository.findById(parentTaskId);
                        if(!tmpTask.get().getStatus()) {
                            System.out.println("Есть не выполненая задача: " + tmpTask.get().getDescription());
                            tmpStatus.set(false);
                            return;
                        }
                    }
            );

            if(tmpStatus.get()) {
                Task.get().setStatus(true);
                taskRepository.save(Task.get());
                System.out.println("Задача отмечена");
            }
        } else {
            Task.get().setStatus(true);
            taskRepository.save(Task.get());
            System.out.println("Задача отмечена");
        }
    }

    //•  получение всех невыполненных ToDo (с сортировками по полям);
    //Можно дополнить фильтром по своему запросу <, >, =
    public void showNotEndTask(Long id){
       List<Task> listTask = taskRepository.getOutstandingTask(userService.getUsers(id));
       List<Comparator<Task>> comparatorList = List.of(
               (id_1, id_2) -> id_1.getId().compareTo(id_2.getId()),
               (id_1, id_2) -> id_1.getMessageSend().compareTo(id_2.getMessageSend()),
               (id_1, id_2) -> id_1.getTaskStartTime().compareTo(id_2.getTaskStartTime()),
               (id_1, id_2) -> id_1.getTaskEndTime().compareTo(id_2.getTaskEndTime())
       );

        System.out.println(
                "По какому полю сортируем: "+
                 "\n[id - 0], [Отправлено ли сообщение - 1], [Время начала задачи - 2], [Время завершения задачи - 3]"
        );

        String action = Input.next().toLowerCase(Locale.ROOT);

        System.out.println(
                "Какой порядок: "+
                 "\n[по возрастанию - 0], [по убыванию - 1]"
        );

        action += Input.next().toLowerCase(Locale.ROOT);

        switch (action) {
            case "00": listTask.sort(comparatorList.get(0)); break;
            case "01": listTask.sort(comparatorList.get(0).reversed()); break;
            case "10": listTask.sort(comparatorList.get(1)); break;
            case "11": listTask.sort(comparatorList.get(1).reversed()); break;
            case "20": listTask.sort(comparatorList.get(2)); break;
            case "21": listTask.sort(comparatorList.get(2).reversed()); break;
            case "30": listTask.sort(comparatorList.get(3)); break;
            case "31": listTask.sort(comparatorList.get(3).reversed()); break;
            default:
                System.out.println("Не вверный ввод или выход"); return;
        }

        listTask.forEach(
                task -> {showInfoTask(task);
                System.out.println();
        }
        );
    }
    //Можно дополнить сортировкой и фильтром по своему запросу <, >, =
    public void showEndTask(Long id){
        taskRepository.getCompletedTasks(userService.getUsers(id)).forEach(this::showInfoTask);
    }

    public void deleteTask(Long id){
        taskRepository.deleteById(getIdTask("Введите название удаляемой задачи: ",id));
    }

    public void updateTask(Long id){
        System.out.println("Пока не реализовано.... Изменяем имя, время окончания, зависимости");
    }

    private Boolean checkLinkTask(Long parentTaskId, Long childTaskId){
        if(parentTaskId.equals(childTaskId)){
            System.out.println("Нельзя указать основную задачу как под задачу!");
            return false;
        }

        List<Long> listParentTask = taskLinksRepository.listParent();
        List<Long> listChildTask = taskLinksRepository.listChild();

        if(listParentTask.contains(parentTaskId) && listChildTask.contains(childTaskId)){
            System.out.println("Связь уже создана.");
            return false;
        }

        if(listParentTask.contains(childTaskId) && listChildTask.contains(parentTaskId)){
            System.out.println("Создание циклической связи!");
            return false;
        }

        return true;
    }

    //•  при выполнении недопустимой операции (Отметка о готовности несуществующего ToDo), возвращать соответствующее сообщение;
    private Long _getIdTask(Long id){
        List<Task> Task;
        String TaskDesc = Input.next();
        Task = taskRepository.findByDescriptionAndUsers(TaskDesc, userService.getUsers(id));

        if (Task.isEmpty()) {
            System.out.println("Нет такой задачи.");
            return 0L;
        }
        if (Task.size() > 1) {
            System.out.println("У вас несколько задач с таким именем:");
            Task.forEach(this::showInfoTask);
            System.out.println("\nуточните задачу (выбирите id): ");
            return Input.nextLong();
        } else {
            return Task.get(0).getId();
        }
    }

    private Long getIdTask(String text, Long id){
        Long tmpIdTask;
        do {
            System.out.print(text);
            tmpIdTask = _getIdTask(id);
            if (!tmpIdTask.equals(0L)) return tmpIdTask;
        } while (true);
    }

    private String requestDescription(){
        String Description;
        do {
            System.out.println("Введите описание:");
            Description = Input.next();

            if (Description.length() <= 255){
                return Description;
            } else {
                System.out.println("Слишком длинное описание! Максимум 255 символов.");
            }
        } while (true);
    }

    private LocalDateTime requestDateEnd(){
        LocalDateTime DateEnd;
        int year, month, dayOfMonth, hour, minute;

        do {
            try {
                System.out.print("Год: "); year = Input.nextInt();
                System.out.print("Месяц: "); month = Input.nextInt();
                System.out.print("День месяца: "); dayOfMonth = Input.nextInt();
                System.out.print("Часы: "); hour = Input.nextInt();
                System.out.print("Минуты: "); minute = Input.nextInt();
            } catch (Exception e){
                System.out.println("Неверный ввод! Необходимо вводить числа.");
                Input.nextLine();
                continue;
            }

            try {
                DateEnd = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
            } catch (Exception e) {
                System.out.println("Неверный ввод! Введены не коректные числа.");
                continue;
            }

            if(LocalDateTime.now().isAfter(DateEnd)){
                System.out.println("Вы ввели некорректную или старую дату!");
                continue;
            }

            return DateEnd;
        }while (true);
    }

    private Long timeToTaskCompletion(LocalDateTime Start, LocalDateTime End){
        return ChronoUnit.MINUTES.between(Start, End);
    }

    private void showInfoTask(Task task){
        Long tmpTimeMin = timeToTaskCompletion(LocalDateTime.now(), task.getTaskEndTime());

        System.out.println(
                "\n\nId задачи: " + task.getId() +
                "\nОписание задачи: " + task.getDescription() +
                "\nДата создания задачи: " + task.getTaskStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                "\nДата завершеня задачи: " + task.getTaskEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                "\nДано времени на задачу в минутах: " + timeToTaskCompletion(task.getTaskStartTime(), task.getTaskEndTime()) +
                "\nОсталось времени до завершения задачи в минутах: " + tmpTimeMin +
                "\nСостояние задачи: " + (task.getStatus() ? "выполнена" : tmpTimeMin > 0 ? "не выполнена" : "просрочена") +
                "\nБыло ли отправлено сообщение: " + (task.getMessageSend() ? "отправлено" : "не отправлено")
        );

        System.out.print("Зависимые задачи: ");
        List<Long> listId = taskLinksRepository.getAllByChild(task);
        if(listId.isEmpty()) System.out.print("отсутствуют");
        else listId.forEach(id -> System.out.print(taskRepository.findById(id).get().getDescription() + " "));

        System.out.print("\nЗадача зависит от задач: ");
        listId = taskLinksRepository.getAllByParent(task);
        if(listId.isEmpty()) System.out.print("отсутствуют\n");
        else listId.forEach(id -> System.out.print(taskRepository.findById(id).get().getDescription() + " "));
    }
}
