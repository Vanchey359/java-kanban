package controller;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int ids = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int createNewId() {
        ids += 1;
        return ids;
    }

    public ArrayList<Task> getAllTask() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(task);
        }
        return allTasks;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }
        return allEpics;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }
        return allSubtasks;
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public int createNewTask(Task task) {
        ids = createNewId();
        task.setId(ids);
        tasks.put(ids, task);
        return ids;
    }

    public int createNewEpic(Epic epic) {
        ids = createNewId();
        epic.setId(ids);
        epics.put(ids, epic);
        calculateEpicStatus(epic);
        return ids;
    }

    public int createNewSubtask(Subtask subtask) {
        ids = createNewId();
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

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                int removeId = subtask.getId();
                subtasks.remove(removeId);
            }
        }
    }

    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        calculateEpicStatus(epics.get(epicId));
    }

    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
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
        } else {
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
}