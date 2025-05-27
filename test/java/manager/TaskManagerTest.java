package java.manager;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        epic = new Epic("Test Epic", "Test Description");
        subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 1);
    }

    @Test
    void addNewTask() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addSubtaskToNonExistentEpic() {
        final int subtaskId = taskManager.addNewSubtask(subtask);
        assertEquals(-1, subtaskId, "Подзадача не должна быть добавлена к несуществующему эпику.");
    }

    @Test
    void updateTask() {
        final int taskId = taskManager.addNewTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        final Task updatedTask = taskManager.getTask(taskId);
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void updateEpic() {
        final int epicId = taskManager.addNewEpic(epic);
        epic.setName("Updated Epic");
        taskManager.updateEpic(epic);
        final Epic updatedEpic = taskManager.getEpic(epicId);
        assertEquals("Updated Epic", updatedEpic.getName(), "Имя эпика не обновлено.");
    }

    @Test
    void updateSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        final Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtask.getStatus(), "Статус подзадачи не обновлен.");
    }

    @Test
    void removeTask() {
        final int taskId = taskManager.addNewTask(task);
        taskManager.removeTask(taskId);
        assertNull(taskManager.getTask(taskId), "Задача не удалена.");
    }

    @Test
    void removeEpic() {
        final int epicId = taskManager.addNewEpic(epic);
        taskManager.removeEpic(epicId);
        assertNull(taskManager.getEpic(epicId), "Эпик не удален.");
    }

    @Test
    void removeSubtask() {
        final int epicId = taskManager.addNewEpic(epic);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        taskManager.removeSubtask(subtaskId);
        assertNull(taskManager.getSubtask(subtaskId), "Подзадача не удалена.");
    }

    @Test
    void getEpicSubtasks() {
        final int epicId = taskManager.addNewEpic(epic);
        subtask.setEpicId(epicId);
        taskManager.addNewSubtask(subtask);
        final List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epicId);
        assertEquals(1, epicSubtasks.size(), "Неверное количество подзадач эпика.");
        assertEquals(subtask, epicSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getHistory() {
        final int taskId = taskManager.addNewTask(task);
        final int epicId = taskManager.addNewEpic(epic);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getSubtask(subtaskId);

        final List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
        assertEquals(task, history.get(0), "Первая задача в истории должна быть Task");
        assertEquals(epic, history.get(1), "Вторая задача в истории должна быть Epic");
        assertEquals(subtask, history.get(2), "Третья задача в истории должна быть Subtask");
    }
}