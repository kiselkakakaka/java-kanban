package manager;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, 1);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, 2);

        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int epic1Id = manager.addNewEpic(epic1);
        int epic2Id = manager.addNewEpic(epic2);
        subtask1.setEpicId(epic1Id);
        subtask2.setEpicId(epic1Id);
        subtask3.setEpicId(epic2Id);
        int subtask1Id = manager.addNewSubtask(subtask1);
        int subtask2Id = manager.addNewSubtask(subtask2);
        int subtask3Id = manager.addNewSubtask(subtask3);

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

        System.out.println("\nИстория просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.removeTask(task1Id);
        manager.removeEpic(epic1Id);
        manager.removeSubtask(subtask3Id);

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
