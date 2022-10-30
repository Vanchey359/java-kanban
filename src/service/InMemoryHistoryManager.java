package service;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    public static List<Task> tasksHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if (tasksHistory.size() > 9) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
