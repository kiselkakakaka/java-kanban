package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import model.exceptions.NotFoundException;
import model.exceptions.IntersectionException;

import java.util.Objects;
import java.util.Set;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int generateId() {
        return nextId++;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicFields(epic);
        });
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) throw new NotFoundException("Task not found with id=" + id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) throw new NotFoundException("Epic not found with id=" + id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) throw new NotFoundException("Subtask not found with id=" + id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addNewTask(Task task) {
        if (hasIntersection(task)) throw new IntersectionException("Task intersects with existing one.");
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) return -1;
        if (hasIntersection(subtask)) throw new IntersectionException("Subtask intersects with existing one.");
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        prioritizedTasks.add(subtask);
        updateEpicFields(epics.get(subtask.getEpicId()));
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) throw new NotFoundException("Task not found with id=" + task.getId());
        prioritizedTasks.remove(tasks.get(task.getId()));
        if (hasIntersection(task)) {
            prioritizedTasks.add(tasks.get(task.getId()));
            throw new IntersectionException("Updated task intersects with existing one.");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) throw new NotFoundException("Epic not found with id=" + epic.getId());
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        updateEpicFields(existingEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) throw new NotFoundException("Subtask not found with id=" + subtask.getId());
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        if (hasIntersection(subtask)) {
            prioritizedTasks.add(subtasks.get(subtask.getId()));
            throw new IntersectionException("Updated subtask intersects with existing one.");
        }
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        updateEpicFields(epics.get(subtask.getEpicId()));
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicFields(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return epics.get(epicId).getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null || t1.getEndTime() == null || t2.getEndTime() == null)
            return false;
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t1.getStartTime().isAfter(t2.getEndTime()));
    }

    protected boolean hasIntersection(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existing -> existing.getId() != newTask.getId() && isOverlapping(existing, newTask));
    }

    protected void updateEpicFields(Epic epic) {
        List<Subtask> subtaskList = getEpicSubtasks(epic.getId());
        if (subtaskList.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        Duration total = subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime start = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(total);

        boolean allDone = subtaskList.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);
        boolean allNew = subtaskList.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);

        if (allDone) epic.setStatus(TaskStatus.DONE);
        else if (allNew) epic.setStatus(TaskStatus.NEW);
        else epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
