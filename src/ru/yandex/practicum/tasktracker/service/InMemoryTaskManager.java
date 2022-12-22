package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected static int currentId = 0;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            if (historyManager.getHistory().contains(task)) {
                historyManager.remove(task.getId());          //// Выявил ошибку при написании теста. При удалении созданных задач, которые не были добавлены в историю - выскакивал NullPointerException в этом месте. Добавил эту проверку для нормализации работы метода.
            }
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            List<Subtask> epicSubtasks = getSubtasksByEpic(epic.getId());
            for (Subtask subtask : epicSubtasks) {
                if (historyManager.getHistory().contains(subtask)) {
                    historyManager.remove(subtask.getId());            //// То же самое что и сверху.
                }
            }
            if (historyManager.getHistory().contains(epic)) {
                historyManager.remove(epic.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (historyManager.getHistory().contains(subtask)) {
                historyManager.remove(subtask.getId());            //// То же самое что и сверху.
            }
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public int createTask(Task task) {
        if (task.getStartTime() != null) {
            chekTimeCrossing(task);
        }
        nextId();
        task.setId(currentId);
        tasks.put(currentId, task);
        return currentId;
    }

    @Override
    public int createEpic(Epic epic) {
        nextId();
        epic.setId(currentId);
        epics.put(currentId, epic);
        calculateEpicStatus(epic);
        return currentId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            chekTimeCrossing(subtask);
        }
        nextId();
        subtask.setId(currentId);
        subtasks.put(currentId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(currentId);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        calculateEpicStartTime(epics.get(subtask.getEpicId()));
        calculateEpicDuration(epics.get(subtask.getEpicId()));
        calculateEpicEndTime(epics.get(subtask.getEpicId()));
        return currentId;
    }

    @Override
    public int updateTask(Task task) {
        if (task.getStartTime() != null) {
                chekTimeCrossing(task);
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null) {
                chekTimeCrossing(subtask);
        }
        subtasks.put(subtask.getId(), subtask);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        calculateEpicStartTime(epics.get(subtask.getEpicId()));
        calculateEpicDuration(epics.get(subtask.getEpicId()));
        calculateEpicEndTime(epics.get(subtask.getEpicId()));
        return subtask.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        return epic.getId();
    }

    @Override
    public void removeTaskById(int taskId) {
        if (historyManager.getHistory().contains(tasks.get(taskId))) {   ////То же самое.
            historyManager.remove(taskId);
        }
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epicId);
        if (!epicSubtasks.isEmpty()) {                          //// То же самое.
            for (Subtask subtask : epicSubtasks) {
                subtasks.remove(subtask.getId());
                if (historyManager.getHistory().contains(subtask)) {   //// То же самое.
                    historyManager.remove(subtask.getId());
                }
            }
        }
        if (historyManager.getHistory().contains(epics.get(epicId))) {   //// То же самое.
            historyManager.remove(epicId);
        }
        epics.remove(epicId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        epics.get(epicId).removeSubtaskId(subtaskId);
        if (historyManager.getHistory().contains(subtasks.get(subtaskId))) { //// То же самое.
            historyManager.remove(subtaskId);
        }
        subtasks.remove(subtaskId);
        calculateEpicStatus(epics.get(epicId));
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void nextId() {
        currentId++;
    }

    private void calculateEpicStatus(Epic epic) {
        int newCounter = 0;
        int doneCounter = 0;
        int inProgressCounter = 0;

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                if (subtask.getStatus().equals(Status.NEW)) {
                    newCounter += 1;
                } else if (subtask.getStatus().equals(Status.DONE)) {
                    doneCounter += 1;
                } else {
                    inProgressCounter += 1;
                }
            }
        }
        if (doneCounter == 0 && inProgressCounter == 0) {
            epic.setStatus(Status.NEW);
        } else if (newCounter == 0 && inProgressCounter == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>();
        prioritizedTasks.addAll(getAllSubtasks());
        prioritizedTasks.addAll(getAllTasks());
        return prioritizedTasks;
    }

    private void calculateEpicStartTime(Epic epic) {
        LocalDateTime epicStartTime = LocalDateTime.of(9999, Month.DECEMBER, 9, 23, 59);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (int id : subtaskIds) {
            if (subtasks.get(id).getStartTime() != null) {
                LocalDateTime subtaskStartTime = subtasks.get(id).getStartTime();
                if (subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
        }
        epic.setStartTime(epicStartTime);
    }

    private void calculateEpicDuration(Epic epic) {
        int epicDuration = 0;
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (int id : subtaskIds) {
            if (subtasks.get(id).getDuration() != null) {
                int subtaskDuration = subtasks.get(id).getDuration();
                epicDuration += subtaskDuration;
            }
        }
        epic.setDuration(epicDuration);
    }

    private void calculateEpicEndTime(Epic epic) {
        LocalDateTime epicEndTime = LocalDateTime.of(1970, Month.JANUARY, 1, 1, 1);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (int id : subtaskIds) {
            if (subtasks.get(id).getEndTime() != null) {
                LocalDateTime subtaskEndTime = subtasks.get(id).getEndTime();
                if (subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
        }
        epic.setEndTime(epicEndTime);
    }

    private void chekTimeCrossing(Task task) {
        TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
        ArrayList<Task> prioritizedTaskList = new ArrayList<>(prioritizedTasks);
            for (Task element : prioritizedTaskList) {
                if (element.getStartTime() != null && element.getEndTime() != null) {
                    try {
                        if (task.getStartTime().isAfter(element.getStartTime()) && task.getStartTime().isBefore(element.getEndTime())) {
                            throw new TimeCrossingException("Время выполнения задачи пересекается с уже существующей задачей!");
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}