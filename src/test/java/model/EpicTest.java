package model;

import model.Epic;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldCreateEpicWithCorrectFields() {
        Epic epic = new Epic("Test Epic", "Test Description");
        assertEquals("Test Epic", epic.getName());
        assertEquals("Test Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void shouldUpdateEpicStatus() {
        Epic epic = new Epic("Test Epic", "Test Description");
        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldAddAndRemoveSubtaskIds() {
        Epic epic = new Epic("Test Epic", "Test Description");
        int subtaskId = 1;
        
        epic.addSubtaskId(subtaskId);
        assertTrue(epic.getSubtaskIds().contains(subtaskId));
        
        epic.removeSubtaskId(subtaskId);
        assertFalse(epic.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void shouldNotAddSelfAsSubtask() {
        Epic epic = new Epic("Test Epic", "Test Description");
        epic.addSubtaskId(epic.getId());
        assertFalse(epic.getSubtaskIds().contains(epic.getId()));
    }
} 