package java.model;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldCreateTaskWithCorrectFields() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        assertEquals("Test Task", task.getName());
        assertEquals("Test Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldUpdateTaskStatus() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldGenerateUniqueId() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void shouldBeEqualIfSameId() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(task1.getId());
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void shouldNotBeEqualIfDifferentId() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertNotEquals(task1, task2, "Задачи с разными id не должны быть равны");
    }
}