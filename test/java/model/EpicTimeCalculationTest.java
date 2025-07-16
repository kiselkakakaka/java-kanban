package java.model;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EpicTimeCalculationTest {

    @Test
    public void shouldCalculateStartEndAndDuration() {
        TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());

        Epic epic = new Epic("Epic with Subtasks", "desc");
        int epicId = manager.addNewEpic(epic);

        Subtask s1 = new Subtask("Sub1", "desc1", TaskStatus.NEW, epicId);
        s1.setStartTime(LocalDateTime.of(2025, 7, 6, 9, 0));
        s1.setDuration(Duration.ofMinutes(45));
        manager.addNewSubtask(s1);

        Subtask s2 = new Subtask("Sub2", "desc2", TaskStatus.NEW, epicId);
        s2.setStartTime(LocalDateTime.of(2025, 7, 6, 10, 30));
        s2.setDuration(Duration.ofMinutes(60));
        manager.addNewSubtask(s2);

        Epic updated = manager.getEpic(epicId);
        assertEquals(LocalDateTime.of(2025, 7, 6, 9, 0), updated.getStartTime());
        assertEquals(LocalDateTime.of(2025, 7, 6, 11, 30), updated.getEndTime());
        assertEquals(Duration.ofMinutes(105), updated.getDuration());
    }

    @Test
    public void shouldReturnNullsForEmptyEpic() {
        TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic = new Epic("Empty Epic", "desc");
        int epicId = manager.addNewEpic(epic);

        Epic updated = manager.getEpic(epicId);
        assertNull(updated.getStartTime());
        assertNull(updated.getEndTime());
        assertEquals(Duration.ZERO, updated.getDuration());
    }
}
