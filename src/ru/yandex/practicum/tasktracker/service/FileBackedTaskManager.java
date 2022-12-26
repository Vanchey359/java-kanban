package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final int TITLE = 2;
    private static final int DESCRIPTION = 4;
    private static final int STATUS = 3;
    private static final int EPIC_ID = 5;
    public static final int ID = 0;
    private static final int START_TIME = 5;
    private static final int DURATION = 6;
    private static final int END_TIME = 7;
    public static final int TASK_TYPE_START_FROM = 1;
    public static final String FIRST_CSV_LINE = "id,type,name,status,description,epic,startTime,duration,endTime" + "\n";

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager restoredManager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] values = line.split(",");
                if (values.length <= TASK_TYPE_START_FROM) {
                    continue;
                }
                String type = values[TASK_TYPE_START_FROM];
                switch (type) {
                    case "TASK":
                        Task task = restoredManager.createTaskFromString(line);
                        currentId = Math.max(currentId, task.getId());
                        break;
                    case "EPIC":
                        Epic epic = restoredManager.createEpicFromString(line);
                        currentId = Math.max(currentId, epic.getId());
                        break;
                    case "SUBTASK":
                        Subtask subtask = restoredManager.createSubtaskFromString(line);
                        restoredManager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                        currentId = Math.max(currentId, subtask.getId());
                        break;
                }
            }
            if (lines.isEmpty()) {
                return restoredManager;
            }
            if (isHistoryLine(lines)) {
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
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Information not saved", e);
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

    private static boolean isHistoryLine(List<String> lines) {
        return !lines.get(lines.size() - 1).contains("TASK")
                && !lines.get(lines.size() - 1).contains("EPIC")
                && !lines.get(lines.size() - 1).contains("SUBTASK");
    }

    private static String historyToString(List<Task> history) {
        return history.stream()
                .map(Task::getId)
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FIRST_CSV_LINE);
            String taskLines = Stream.of(getAllTasks(), getAllEpics(), getAllSubtasks())
                    .flatMap(List::stream)
                    .map(Task::toCsvRow)
                    .collect(Collectors.joining("\n"));
            fileWriter.write(taskLines + "\n");
            if (!historyManager.getHistory().isEmpty()) {
                fileWriter.write("\n");
                fileWriter.write(historyToString(historyManager.getHistory()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Information not saved", e);
        }
    }

    private Task createTaskFromString(String value) {
        String[] taskValue = value.split(",");
        Task task = new Task();
        task.setTitle(taskValue[TITLE]);
        task.setDescription(taskValue[DESCRIPTION]);
        task.setStatus(Status.valueOf(taskValue[STATUS]));
        task.setId(Integer.parseInt(taskValue[ID]));
        if (!taskValue[START_TIME].contains("null")) {
            task.setStartTime(LocalDateTime.parse(taskValue[START_TIME]));
        }
        if (!taskValue[DURATION].contains("null")) {
            task.setDuration(Integer.valueOf(taskValue[DURATION]));
        }
        super.updateTask(task);
        return task;
    }

    private Epic createEpicFromString(String value) {
        String[] epicValue = value.split(",");
        Epic epic = new Epic();
        epic.setTitle(epicValue[TITLE]);
        epic.setDescription(epicValue[DESCRIPTION]);
        epic.setStatus(Status.valueOf(epicValue[STATUS]));
        epic.setId(Integer.parseInt(epicValue[ID]));
        if (!epicValue[START_TIME].contains("null")) {
            epic.setStartTime(LocalDateTime.parse(epicValue[START_TIME]));
        }
        if (!epicValue[DURATION].contains("null")) {
            epic.setDuration(Integer.valueOf(epicValue[DURATION]));
        }
        if (!epicValue[END_TIME].contains("null")) {
            epic.setEndTime(LocalDateTime.parse(epicValue[END_TIME]));
        }
        super.updateEpic(epic);
        return epic;
    }

    private Subtask createSubtaskFromString(String value) {
        String[] subtaskValue = value.split(",");
        Subtask subtask = new Subtask();
        subtask.setTitle(subtaskValue[TITLE]);
        subtask.setDescription(subtaskValue[DESCRIPTION]);
        subtask.setStatus(Status.valueOf(subtaskValue[STATUS]));
        subtask.setEpicId(Integer.parseInt(subtaskValue[EPIC_ID]));
        subtask.setId(Integer.parseInt(subtaskValue[ID]));
        if (!subtaskValue[START_TIME + 1].contains("null")) {
            subtask.setStartTime(LocalDateTime.parse(subtaskValue[START_TIME + 1]));
        }
        if (!subtaskValue[DURATION + 1].contains("null")) {
            subtask.setDuration(Integer.valueOf(subtaskValue[DURATION + 1]));
        }
        super.updateSubtask(subtask);
        return subtask;
    }

    private static List<Integer> historyFromString(String value) {
        String[] historyIdsArray = value.split(",");
        return Arrays.stream(historyIdsArray)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
