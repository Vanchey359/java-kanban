package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int taskId);

    List<Task> getHistory();
}
