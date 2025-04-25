package manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

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
        task.setId(10); // важно установить ID!
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дубликаты задач не должны добавляться в историю");
        assertEquals(task, history.get(history.size() - 1), "Повторная задача должна быть в конце");
    }


    @Test
    void shouldStoreAllTasksWithoutLimit() {
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i, "Description " + i, TaskStatus.NEW);
            task.setId(i); // обязательно установить ID!
            historyManager.add(task);
        }
        assertEquals(100, historyManager.getHistory().size(), "История должна быть неограниченной");
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

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task("Task to remove", "desc", TaskStatus.NEW);
        task.setId(101);
        historyManager.add(task);
        historyManager.remove(101);
        assertFalse(historyManager.getHistory().contains(task), "Задача должна быть удалена из истории");
    }

    @Test
    void shouldMoveTaskToEndIfViewedAgain() {
        Task t1 = new Task("t1", "", TaskStatus.NEW); t1.setId(1);
        Task t2 = new Task("t2", "", TaskStatus.NEW); t2.setId(2);
        Task t3 = new Task("t3", "", TaskStatus.NEW); t3.setId(3);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);
        historyManager.add(t1);

        List<Task> history = historyManager.getHistory();
        assertEquals(t2, history.get(0));
        assertEquals(t3, history.get(1));
        assertEquals(t1, history.get(2));
    }

}

