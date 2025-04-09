package manager;

import model.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        
        history.remove(task); // Удаляем задачу, если она уже есть в истории
        history.addLast(task); // Добавляем задачу в конец списка
        
        // Если размер истории превышает максимальный, удаляем самый старый элемент
        while (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
