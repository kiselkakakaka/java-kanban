package model;

import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldCreateSubtaskWithCorrectFields() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 1);
        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Test Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void shouldUpdateSubtaskStatus() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 1);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void shouldUpdateEpicId() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 1);
        subtask.setEpicId(2);
        assertEquals(2, subtask.getEpicId());
    }

    @Test
    void shouldNotBeOwnEpic() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 1);
        subtask.setEpicId(subtask.getId());
        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }
} 