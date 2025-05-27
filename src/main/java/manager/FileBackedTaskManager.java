package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (file.length() == 0) {
            return manager;
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                Task task = stringToTask(line);
                switch (task.getType()) {
                    case TASK -> manager.addNewTask(task);
                    case EPIC -> manager.addNewEpic((Epic) task);
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        manager.addNewSubtask(subtask);
                        Epic epic = manager.getEpic(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла", e);
        }
        return manager;
    }

    private static String taskToString(Task task) {
        String epicId = (task instanceof Subtask subtask) ? String.valueOf(subtask.getEpicId()) : "";
        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    private static Task stringToTask(String line) {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        return switch (type) {
            case TASK -> new Task(id, name, description, status);
            case EPIC -> new Epic(id, name, description, status);
            case SUBTASK -> new Subtask(id, name, description, status, Integer.parseInt(fields[5]));
        };
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    public static void main(String[] args) {
        try {
            File file = File.createTempFile("autosave", ".csv");
            file.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            Task task = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW);
            Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1", TaskStatus.NEW);
            Subtask sub = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 2);

            manager.addNewTask(task);
            manager.addNewEpic(epic);
            manager.addNewSubtask(sub);

            FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
            System.out.println("Загружено:");
            loaded.getTasks().forEach(System.out::println);
            loaded.getEpics().forEach(System.out::println);
            loaded.getSubtasks().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Ошибка при работе с временным файлом: " + e.getMessage());
        }
    }
}
