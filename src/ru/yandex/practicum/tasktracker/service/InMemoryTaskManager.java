package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (int epicId : epics.keySet()) {
            List<Subtask> epicSubtasks = getSubtasksByEpic(epicId);
            for (Subtask subtask : epicSubtasks) {
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(epicId);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
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
        checkTimeCrossing(task);
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
        checkTimeCrossing(subtask);
        nextId();
        subtask.setId(currentId);
        subtasks.put(currentId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(currentId);
        updateEpic(epics.get(subtask.getEpicId()));
        return currentId;
    }

    @Override
    public int updateTask(Task task) {
        checkTimeCrossing(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        checkTimeCrossing(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epics.get(subtask.getEpicId()));
        return subtask.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        calculateEpicStartTime(epic);
        calculateEpicDuration(epic);
        calculateEpicEndTime(epic);
        return epic.getId();
    }

    @Override
    public void removeTaskById(int taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epicId);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        epics.get(epicId).removeSubtaskId(subtaskId);
        historyManager.remove(subtaskId);
        subtasks.remove(subtaskId);
        calculateEpicStatus(epics.get(epicId));
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        List<Subtask> allSubtasks = new ArrayList<>(subtasks.values());
        return allSubtasks.stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
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
        TreeSet<Task> prioritizedTask = new TreeSet<>();
        prioritizedTask.addAll(getAllTasks());
        prioritizedTask.addAll(getAllSubtasks());
        return prioritizedTask;
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

    private void checkTimeCrossing(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task element : prioritizedTasks) {
            if (element.getStartTime() == null && element.getEndTime() == null) {
                continue;
            }
            if (task.getStartTime().isAfter(element.getStartTime()) && task.getStartTime().isBefore(element.getEndTime())) {
                throw new TimeCrossingException("The execution time of the task overlaps with an already existing task!");
            }
        }
    }
}