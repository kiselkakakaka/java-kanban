package manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        historyManager.add(task);
        assertTrue(historyManager.getHistory().contains(task), "Задача должна быть добавлена в историю");
    }

    @Test
    void shouldNotAddDuplicateTask() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "Дубликаты задач не должны добавляться в историю");
    }

    @Test
    void shouldLimitHistorySize() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Description " + i, TaskStatus.NEW);
            historyManager.add(task);
        }
        assertEquals(10, historyManager.getHistory().size(), "История должна содержать не более 10 задач");
    }

    @Test
    void shouldPreserveTaskData() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        historyManager.add(task);
        Task savedTask = historyManager.getHistory().get(0);
        assertEquals(task.getName(), savedTask.getName(), "Имя задачи должно сохраниться");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи должно сохраниться");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи должен сохраниться");
    }
} 