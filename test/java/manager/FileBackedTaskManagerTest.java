package java.manager;

import manager.FileBackedTaskManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() throws IOException {
        file = Files.createTempFile("kanban", ".csv").toFile();
        file.deleteOnExit(); // автоматически удалит файл после завершения JVM
        manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), file);
    }
}
