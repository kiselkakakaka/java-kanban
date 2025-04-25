package manager;

import manager.Managers;
import manager.TaskManager;
import manager.HistoryManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не должен быть null");
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не должен быть null");
    }

    @Test
    void shouldAddTaskToManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);
        assertNotNull(taskManager.getTask(taskId), "Задача должна быть добавлена в менеджер");
    }
}