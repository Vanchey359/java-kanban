package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final int TITLE = 2;
    private static final int DESCRIPTION = 4;
    private static final int STATUS = 3;
    private static final int EPICID = 5;
    public static final int ID = 0;

    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager restoredManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String task : lines) {
                if (task.startsWith("TASK", 2)) {
                    restoredManager.CreateTaskFromString(task);
                } else if (task.startsWith("EPIC", 2)) {
                    restoredManager.CreateEpicFromString(task);
                } else if (task.startsWith("SUBTASK", 2)) {
                    restoredManager.CreateSubtaskFromString(task);
                }
            }

            List<Integer> restoredHistory = historyFromString(lines.get(lines.size() - 1));
            for (Integer id : restoredHistory) {
                if (restoredManager.tasks.get(id) != null) {
                    restoredManager.historyManager.add(restoredManager.getTaskById(id));
                } else if (restoredManager.epics.get(id) != null) {
                    restoredManager.historyManager.add(restoredManager.getEpicById(id));
                } else if (restoredManager.subtasks.get(id) != null) {
                    restoredManager.historyManager.add(restoredManager.getSubtaskById(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Information not saved");
        }
        return restoredManager;
    }


    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public int createTask(Task task) {
        int currentId = super.createTask(task);
        save();
        return currentId;
    }

    @Override
    public int createEpic(Epic epic) {
        int currentId = super.createEpic(epic);
        save();
        return currentId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int currentId = super.createSubtask(subtask);
        save();
        return currentId;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder historyIds = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyIds.append(task.getId()).append(",");
        }
        historyIds.deleteCharAt(historyIds.length() - 1);
        return historyIds.toString();
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            String firstCsv = "id,type,name,status,description,epic" + System.lineSeparator();
            fileWriter.write(firstCsv);
            for (Task task : getAllTasks()) {
                fileWriter.write(task.toCsvRow() + System.lineSeparator());
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(epic.toCsvRow() + System.lineSeparator());
            }
            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(subtask.toCsvRow() + System.lineSeparator());
            }
            if (!historyManager.getHistory().isEmpty()) {
                fileWriter.write("\n");
                fileWriter.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Information not saved");
        }
    }


    private void CreateTaskFromString(String value) {
        String[] taskValue = value.split(",");
        Task task = new Task();
        task.setTitle(taskValue[TITLE]);
        task.setDescription(taskValue[DESCRIPTION]);
        task.setStatus(Status.valueOf(taskValue[STATUS]));
        task.setId(Integer.parseInt(taskValue[ID]));
        updateTask(task);
    }

    private void CreateEpicFromString(String value) {
        String[] epicValue = value.split(",");
        Epic epic = new Epic();
        epic.setTitle(epicValue[TITLE]);
        epic.setDescription(epicValue[DESCRIPTION]);
        epic.setStatus(Status.valueOf(epicValue[STATUS]));
        epic.setId(Integer.parseInt(epicValue[ID]));
        updateEpic(epic);
    }

    private void CreateSubtaskFromString(String value) {
        String[] subtaskValue = value.split(",");
        Subtask subtask = new Subtask();
        subtask.setTitle(subtaskValue[TITLE]);
        subtask.setDescription(subtaskValue[DESCRIPTION]);
        subtask.setStatus(Status.valueOf(subtaskValue[STATUS]));
        subtask.setEpicId(Integer.parseInt(subtaskValue[EPICID]));
        subtask.setId(Integer.parseInt(subtaskValue[ID]));
        updateSubtask(subtask);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] historyIdsArray = value.split(",");
        for (String id : historyIdsArray) {
            historyIds.add(Integer.valueOf(id));
        }
        return historyIds;
    }


    public static void main(String[] args) {
        FileBackedTaskManager backedManager = new FileBackedTaskManager(new File("History.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        final int taskId1 = backedManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        final int taskId2 = backedManager.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = backedManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = backedManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = backedManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = backedManager.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = backedManager.createEpic(epic2);

        backedManager.getTaskById(taskId1);
        backedManager.getEpicById(epicId1);
        backedManager.getSubtaskById(subtaskId3);


            FileBackedTaskManager restoredManager = loadFromFile(new File("History.csv"));
    }

}
