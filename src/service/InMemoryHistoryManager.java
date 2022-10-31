package service;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> tasksHistory = new ArrayList<>();

    private final int HISTORY_SIZE = 9;

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if (tasksHistory.size() > HISTORY_SIZE) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
