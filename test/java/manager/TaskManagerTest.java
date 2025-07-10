package java.manager;

import manager.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        manager = createManager();
    }

    @Test
    public void testEpicStatus_AllNew() {
        Epic epic = new Epic("E", "e");
        int eid = manager.addNewEpic(epic);

        LocalDateTime base = LocalDateTime.of(2025, 7, 6, 10, 0);

        Subtask s1 = new Subtask("S1", "s1", TaskStatus.NEW, eid);
        s1.setStartTime(base);
        s1.setDuration(Duration.ofMinutes(10));
        manager.addNewSubtask(s1);

        Subtask s2 = new Subtask("S2", "s2", TaskStatus.NEW, eid);
        s2.setStartTime(base.plusMinutes(11));  // избегаем пересечения
        s2.setDuration(Duration.ofMinutes(20));
        manager.addNewSubtask(s2);

        Epic result = manager.getEpic(eid).orElseThrow();
        assertEquals(TaskStatus.NEW, result.getStatus());
    }

    @Test
    public void testEpicStatus_AllDone() {
        Epic epic = new Epic("E", "e");
        int eid = manager.addNewEpic(epic);

        LocalDateTime base = LocalDateTime.of(2025, 7, 6, 12, 0);

        Subtask s1 = new Subtask("S1", "s1", TaskStatus.DONE, eid);
        s1.setStartTime(base);
        s1.setDuration(Duration.ofMinutes(10));
        manager.addNewSubtask(s1);

        Subtask s2 = new Subtask("S2", "s2", TaskStatus.DONE, eid);
        s2.setStartTime(base.plusMinutes(11));
        s2.setDuration(Duration.ofMinutes(20));
        manager.addNewSubtask(s2);

        Epic result = manager.getEpic(eid).orElse(null);
        assertEquals(TaskStatus.DONE, result.getStatus());
    }

    @Test
    public void testEpicStatus_InProgressMix() {
        Epic epic = new Epic("E", "e");
        int eid = manager.addNewEpic(epic);

        LocalDateTime base = LocalDateTime.of(2025, 7, 6, 14, 0);

        Subtask s1 = new Subtask("S1", "s1", TaskStatus.NEW, eid);
        s1.setStartTime(base);
        s1.setDuration(Duration.ofMinutes(10));
        manager.addNewSubtask(s1);

        Subtask s2 = new Subtask("S2", "s2", TaskStatus.DONE, eid);
        s2.setStartTime(base.plusMinutes(11));
        s2.setDuration(Duration.ofMinutes(20));
        manager.addNewSubtask(s2);

        Epic result = manager.getEpic(eid).orElse(null);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    public void testEpicStatus_InProgressSubtask() {
        Epic epic = new Epic("E", "e");
        int eid = manager.addNewEpic(epic);

        LocalDateTime start = LocalDateTime.of(2025, 7, 6, 16, 0);

        Subtask s1 = new Subtask("S1", "s1", TaskStatus.IN_PROGRESS, eid);
        s1.setStartTime(start);
        s1.setDuration(Duration.ofMinutes(10));
        manager.addNewSubtask(s1);

        Epic result = manager.getEpic(eid).orElseThrow();
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    public void testNoIntersection() {
        Task t1 = new Task("T1", "t1", TaskStatus.NEW);
        t1.setStartTime(LocalDateTime.of(2025, 7, 6, 17, 0));
        t1.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(t1);

        Task t2 = new Task("T2", "t2", TaskStatus.NEW);
        t2.setStartTime(t1.getEndTime().plusMinutes(1));
        t2.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> manager.addNewTask(t2));
    }

    @Test
    public void testIntersectionThrows() {
        Task t1 = new Task("T1", "t1", TaskStatus.NEW);
        t1.setStartTime(LocalDateTime.of(2025, 7, 6, 18, 0));
        t1.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(t1);

        Task t2 = new Task("T2", "t2", TaskStatus.NEW);
        t2.setStartTime(t1.getEndTime().minusMinutes(1)); // конфликт
        t2.setDuration(Duration.ofMinutes(10));
        assertThrows(IllegalArgumentException.class, () -> manager.addNewTask(t2));
    }

    @Test
    public void testPrioritizedSorting() {
        Task early = new Task("Early", "t", TaskStatus.NEW);
        early.setStartTime(LocalDateTime.of(2025, 7, 6, 9, 0));
        early.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(early);

        Task later = new Task("Later", "t", TaskStatus.NEW);
        later.setStartTime(LocalDateTime.of(2025, 7, 6, 12, 0));
        later.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(later);

        assertEquals(early, manager.getPrioritizedTasks().get(0));
        assertEquals(later, manager.getPrioritizedTasks().get(1));
    }
}
