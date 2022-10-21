package service;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int ids = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int createNewId() {
        ids += 1;
        return ids;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            calculateEpicStatus(epic);
        }
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public int createNewTask(Task task) {
        createNewId();
        task.setId(ids);
        tasks.put(ids, task);
        return ids;
    }

    public int createNewEpic(Epic epic) {
        createNewId();
        epic.setId(ids);
        epics.put(ids, epic);
        calculateEpicStatus(epic);
        return ids;
    }

    public int createNewSubtask(Subtask subtask) {
        createNewId();
        subtask.setId(ids);
        subtasks.put(ids, subtask);
        epics.get(subtask.getEpicId()).getSubtaskIds().add(ids);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        return ids;
    }

    public int updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calculateEpicStatus(epics.get(subtask.getEpicId()));
        return subtask.getId();
    }

    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        return epic.getId();
    }

    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpicById(int epicId) {
        epics.remove(epicId);
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                int removeId = subtask.getId();
                subtasks.remove(removeId);
            }
        }
    }

    public void removeSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getEpicId();
        subtasks.remove(subtaskId);
        epics.get(epicId).getSubtaskIds().remove(subtaskId);
        calculateEpicStatus(epics.get(epicId));
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    private void calculateEpicStatus(Epic epic) {
        int newCounter = 0;
        int doneCounter = 0;
        int inProgressCounter = 0;

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus("NEW");
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                if (subtask.getStatus().equals("NEW")) {
                    newCounter += 1;
                } else if (subtask.getStatus().equals("DONE")) {
                    doneCounter += 1;
                } else {
                    inProgressCounter += 1;
                    }
                }
            }
            if (doneCounter == 0 && inProgressCounter == 0) {
                epic.setStatus("NEW");
            } else if (newCounter == 0 && inProgressCounter == 0) {
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
    }