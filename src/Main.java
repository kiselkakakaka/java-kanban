public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создаем задачи
        Task task1 = manager.createTask("Задача 1", "Описание задачи 1");
        Task task2 = manager.createTask("Задача 2", "Описание задачи 2");

        // Создаем эпики с подзадачами
        Epic epic1 = manager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = manager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = manager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());

        Epic epic2 = manager.createEpic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = manager.createSubtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());

        // Выводим списки задач
        printAllTasks(manager);

        // Изменяем статусы
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);

        // Выводим обновленные данные
        System.out.println("\nПосле изменения статусов:");
        printTaskInfo(manager, task1, subtask1, subtask2, subtask3, epic1, epic2);

        // Получаем подзадачи эпика
        System.out.println("\nПодзадачи Эпика 1:");
        manager.getSubtasksByEpic(epic1.getId()).forEach(System.out::println);

        // Удаляем одну задачу и один эпик
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId());

        // Выводим итоговые списки
        System.out.println("\nПосле удаления:");
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Все задачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        manager.getAllEpics().forEach(System.out::println);
    }

    private static void printTaskInfo(TaskManager manager, Task task, Subtask subtask1,
                                      Subtask subtask2, Subtask subtask3, Epic epic1, Epic epic2) {
        System.out.println("Задача 1: " + manager.getTask(task.getId()));
        System.out.println("Подзадача 1: " + manager.getSubtask(subtask1.getId()));
        System.out.println("Подзадача 2: " + manager.getSubtask(subtask2.getId()));
        System.out.println("Подзадача 3: " + manager.getSubtask(subtask3.getId()));
        System.out.println("Эпик 1: " + manager.getEpic(epic1.getId()));
        System.out.println("Эпик 2: " + manager.getEpic(epic2.getId()));
    }
}