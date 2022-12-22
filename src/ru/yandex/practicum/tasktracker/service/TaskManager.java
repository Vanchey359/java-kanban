package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.util.List;
import java.util.TreeSet;


public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    int updateTask(Task task);

    int updateSubtask(Subtask subtask);

    int updateEpic(Epic epic);

    void removeTaskById(int taskId);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    List<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}