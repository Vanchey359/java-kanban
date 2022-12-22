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
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final int TITLE = 2;
    private static final int DESCRIPTION = 4;
    private static final int STATUS = 3;
    private static final int EPIC_ID = 5;
    public static final int ID = 0;
    private static final int START_TIME = 5;
    private static final int DURATION = 6;
    private static final int END_TIME = 7;
    public static final int TASK_TYPE_START_FROM = 2;
    public static final String FIRST_CSV_LINE = "id,type,name,status,description,epic,startTime,duration,endTime" + System.lineSeparator();

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager restoredManager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.startsWith("TASK", TASK_TYPE_START_FROM)) {
                    Task task = restoredManager.createTaskFromString(line);
                    if (task.getId() > currentId) {
                        currentId = task.getId();
                    }
                } else if (line.startsWith("EPIC", TASK_TYPE_START_FROM)) {
                    Epic epic = restoredManager.createEpicFromString(line);
                    if (epic.getId() > currentId) {
                        currentId = epic.getId();
                    }
                } else if (line.startsWith("SUBTASK", TASK_TYPE_START_FROM)) {
                    Subtask subtask = restoredManager.createSubtaskFromString(line);
                    restoredManager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());    //// Выявил при тестах - subtaskIds не присваивались эпикам при записи в файл, следовательно, не появлялись и при загрузке из файла.
                    if (subtask.getId() > currentId) {
                        currentId = subtask.getId();
                    }
                }
            }

            if (!lines.get(lines.size() - 1).contains("TASK") && !lines.get(lines.size() - 1).contains("EPIC") && !lines.get(lines.size() - 1).contains("SUBTASK")) { /// Выявил при тестах - Принимал предпоследнюю строку, если история пустая то принимал строку с задачей.
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
            throw new ManagerSaveException("Information not saved" , e);
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
            fileWriter.write(FIRST_CSV_LINE);
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
        if (!taskValue[END_TIME].contains("null")) {
            task.setEndTime(LocalDateTime.parse(taskValue[END_TIME]));
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
        if (!subtaskValue[END_TIME].contains("null")) {
            subtask.setEndTime(LocalDateTime.parse(subtaskValue[END_TIME + 1]));
        }
        super.updateSubtask(subtask);
        return subtask;
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

        FileBackedTaskManager backedManager = new FileBackedTaskManager(new File("resources/tasks.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        task1.setEndTime(task1.getEndTime());
        final int taskId1 = backedManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(15);
        task2.setEndTime(task2.getEndTime());
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
        subtask1.setStartTime(task2.getEndTime().plusMinutes(1));
        subtask1.setDuration(20);
        subtask1.setEndTime(subtask1.getEndTime());
        final int subtaskId1 = backedManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        subtask2.setStartTime(subtask1.getEndTime().plusMinutes(1));
        subtask2.setDuration(10);
        subtask2.setEndTime(subtask2.getEndTime());
        final int subtaskId2 = backedManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        subtask3.setStartTime(subtask2.getEndTime().plusMinutes(1));
        subtask3.setDuration(30);
        subtask3.setEndTime(subtask3.getEndTime());
        final int subtaskId3 = backedManager.createSubtask(subtask3);

        Task task6 = new Task();
        task6.setTitle("Task#6");
        task6.setDescription("Task6 description");
        task6.setStatus(Status.NEW);
        final int taskId6 = backedManager.createTask(task6);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = backedManager.createEpic(epic2);

        Subtask subtask6 = new Subtask();
        subtask6.setTitle("Subtask#6-1");
        subtask6.setDescription("Subtask6-1 description");
        subtask6.setStatus(Status.IN_PROGRESS);
        subtask6.setEpicId(epicId1);
        final int subtaskId6 = backedManager.createSubtask(subtask6);

        backedManager.getTaskById(taskId1);
        backedManager.getEpicById(epicId1);
        backedManager.getSubtaskById(subtaskId3);


        FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasks.csv"));    //// Так как появились тесты - main тут и из корня проекта удалять?

        System.out.println(restoredManager.getPrioritizedTasks());  // В этой проверке видно сортировку task и subtask в TreeSet, а так же работу по восстановлению из файла. Сначала идут элементы по порядку от более раннего времени начала к более позднему,
                                                                    // после в конец добавляются элементы без заданных параметров времени.

        // По моему я написал ужасный Спагетти-код и сам же в нем запутался, но оно каким то чудом работает, а дедлайн уже близко...
    }

}
