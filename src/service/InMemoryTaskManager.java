package service;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static task.Status.DONE;
import static task.Status.IN_PROGRESS;
import static task.Status.NEW;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();

    private int currentId = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();


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
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(NEW);
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
        nextId();
        subtask.setId(currentId);
        subtasks.put(currentId, subtask);
        epics.get(subtask.getEpicId()).getSubtaskIds().add(currentId);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        return currentId;
    }

    @Override
    public int updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
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
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epicId);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        subtasks.remove(subtaskId);
        epics.get(epicId).getSubtaskIds().remove(subtaskId);
        calculateEpicStatus(epics.get(epicId));
        historyManager.remove(subtaskId);
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
            epic.setStatus(NEW);
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                if (subtask.getStatus().equals(NEW)) {
                    newCounter += 1;
                } else if (subtask.getStatus().equals(DONE)) {
                    doneCounter += 1;
                } else {
                    inProgressCounter += 1;
                }
            }
        }
        if (doneCounter == 0 && inProgressCounter == 0) {
            epic.setStatus(NEW);
        } else if (newCounter == 0 && inProgressCounter == 0) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }
}