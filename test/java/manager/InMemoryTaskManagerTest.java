package manager;

import org.junit.jupiter.api.BeforeEach;
import manager.TaskManagerTest;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}