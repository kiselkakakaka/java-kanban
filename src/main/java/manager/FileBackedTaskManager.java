package manager;

import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                if (task instanceof Epic) epics.put(task.getId(), (Epic) task);
                else if (task instanceof Subtask) subtasks.put(task.getId(), (Subtask) task);
                else tasks.put(task.getId(), task);
                if (!(task instanceof Epic)) prioritizedTasks.add(task);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading file", e);
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : tasks.values()) writer.write(toString(task) + "\n");
            for (Epic epic : epics.values()) writer.write(toString(epic) + "\n");
            for (Subtask sub : subtasks.values()) writer.write(toString(sub) + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    private String toString(Task task) {
        String base = String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "",
                (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "",
                (task.getStartTime() != null) ? task.getStartTime().format(FORMATTER) : ""
        );
        return base;
    }

    private Task fromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        String epicIdStr = parts[5];
        String durationStr = parts[6];
        String startTimeStr = parts[7];

        Duration duration = (durationStr != null && !durationStr.isEmpty()) ? Duration.ofMinutes(Long.parseLong(durationStr)) : null;
        LocalDateTime startTime = (startTimeStr != null && !startTimeStr.isEmpty()) ? LocalDateTime.parse(startTimeStr, FORMATTER) : null;

        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, name, description, status);
                break;
            case EPIC:
                task = new Epic(id, name, description, status);
                break;
            case SUBTASK:
                task = new Subtask(id, name, description, status, Integer.parseInt(epicIdStr));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        task.setDuration(duration);
        task.setStartTime(startTime);
        return task;
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

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
