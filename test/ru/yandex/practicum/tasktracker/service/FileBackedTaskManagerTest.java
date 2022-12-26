package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try (BufferedWriter bf = Files.newBufferedWriter(Path.of("resources/restored-manager-tests.csv"),
                StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileBackedTaskManager.loadFromFile(new File("resources/restored-manager-tests.csv"));
    }

    @Test
    void save_shouldSaveTasksSubtaskAndEpicToFile() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("resources/restored-manager-test-save.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        fileBackedTaskManager.createTask(task1);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpicId(epicId);
        subtask1.setStartTime(task1.getEndTime().plusMinutes(1));
        subtask1.setDuration(20);
        fileBackedTaskManager.createSubtask(subtask1);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" + task1.getId() +
                ",TASK,Task#1,NEW,Task1 description," + task1.getStartTime() + "," + task1.getDuration()
                + "," + task1.getEndTime() + "\n" + epic1.getId() +
                ",EPIC,Epic#1,IN_PROGRESS,Epic1 description," + epic1.getStartTime() +
                "," + epic1.getDuration() + "," + epic1.getEndTime() + "\n" + subtask1.getId() +
                ",SUBTASK,Subtask#1-1,IN_PROGRESS,Subtask1-1 description," + subtask1.getEpicId() +
                "," + subtask1.getStartTime() + "," + subtask1.getDuration() + "," + subtask1.getEndTime() + "\n"; // Делаю так, а не читаю строку из файла, потому что startTime и endTime будут меняться при каждом вызове. Можно убрать дату из тасков и сделать через файл или оставить так?

        String actual = Files.readString(Path.of("resources/restored-manager-test-save.csv"));

        assertEquals(expected, actual, "Tasks have not been saved");
    }

    @Test
    void loadFromFile_shouldLoadSavedTaskSubtasksAndEpicFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("resources/restored-manager-test-load.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        fileBackedTaskManager.createTask(task1);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpicId(epicId);
        subtask1.setStartTime(task1.getEndTime().plusMinutes(1));
        subtask1.setDuration(20);
        fileBackedTaskManager.createSubtask(subtask1);

        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(new File("resources/restored-manager-test-load.csv"));

        List<Task> expected = new ArrayList<>();
        expected.addAll(fileBackedTaskManager.getAllTasks());
        expected.addAll(fileBackedTaskManager.getAllSubtasks());
        expected.addAll(fileBackedTaskManager.getAllEpics());

        List<Task> actual = new ArrayList<>();
        actual.addAll(restoredManager.getAllTasks());
        actual.addAll(restoredManager.getAllSubtasks());
        actual.addAll(restoredManager.getAllEpics());

        assertEquals(expected, actual, "Tasks not loaded from file");

    }
}