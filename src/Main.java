public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("Creating tasks and epics...");
        Task task1 = manager.createTask(new Task("Task 1", "Description 1", Status.NEW));
        Task task2 = manager.createTask(new Task("Task 2", "Description 2", Status.NEW));

        Epic epic1 = manager.createEpic(new Epic("Epic 1", "Description Epic 1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", Status.NEW, epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask 2", "Description Subtask 2", Status.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Epic 2", "Description Epic 2"));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask 3", "Description Subtask 3", Status.NEW, epic2.getId()));

        printAllTasks(manager);

        System.out.println("\nUpdating task and subtask statuses...");
        task1.setStatus(Status.DONE);
        manager.updateTask(task1);

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        printAllTasks(manager);

        System.out.println("\nDeleting task1 and epic1...");
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId());

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nAll Tasks:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("\nAll Epics:");
        manager.getAllEpics().forEach(System.out::println);

        System.out.println("\nAll Subtasks:");
        manager.getAllSubtasks().forEach(System.out::println);
    }
}