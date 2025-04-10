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

    @Test
    void shouldBeEqualIfSameId() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic2.setId(epic1.getId());
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void shouldNotBeEqualIfDifferentId() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 1", "Description 1");
        assertNotEquals(epic1, epic2, "Эпики с разными id не должны быть равны");
    }
} 