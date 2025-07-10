import manager.Managers;
import manager.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 6, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 6, 11, 0));
        task2.setDuration(Duration.ofMinutes(45));
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        int epic1Id = manager.addNewEpic(epic1);
        int epic2Id = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1Id);
        subtask1.setStartTime(LocalDateTime.of(2025, 7, 6, 12, 0));
        subtask1.setDuration(Duration.ofMinutes(60));

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1Id);
        subtask2.setStartTime(LocalDateTime.of(2025, 7, 6, 13, 30));
        subtask2.setDuration(Duration.ofMinutes(45));

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, epic2Id);
        subtask3.setStartTime(LocalDateTime.of(2025, 7, 6, 15, 0));
        subtask3.setDuration(Duration.ofMinutes(50));

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            System.out.println("Подзадачи эпика:");
            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        System.out.println("\nОбновленные задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("\nОбновленные эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            System.out.println("Подзадачи эпика:");
            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        manager.getTask(task1.getId());
        manager.getEpic(epic1Id);
        manager.getSubtask(subtask1.getId());

        System.out.println("\nИстория просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.removeTask(task1.getId());
        manager.removeEpic(epic1Id);
        manager.removeSubtask(subtask3.getId());

        System.out.println("\nОставшиеся задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("\nОставшиеся эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            System.out.println("Подзадачи эпика:");
            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("\nОставшиеся подзадачи:");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}
