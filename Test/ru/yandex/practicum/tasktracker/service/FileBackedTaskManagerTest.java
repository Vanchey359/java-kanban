package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.tasktracker.service.FileBackedTaskManager.loadFromFile;


public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    private FileBackedTaskManager fileBackedTaskManager;


    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("resources/tasksTest.csv"));
    }

    @Test
    void createTask_shouldSaveTaskInFile() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(10);
        final int taskId = fileBackedTaskManager.createTask(task);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
                taskId + ",TASK,Task#1,NEW,Task1 description," + task.getStartTime() + "," + task.getDuration() + "," + task.getEndTime() + "\n";

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The task was not saved to a file!");
    }

    @Test
    void createEpic_shouldSaveEpicInFile() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,NEW,Epic1 description,null,null,null" + "\n";

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The epic was not saved to a file!");
    }

    @Test
    void createSubtask_shouldSaveSubtaskInFile() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = fileBackedTaskManager.createSubtask(subtask);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,IN_PROGRESS,Epic1 description,9999-12-09T23:59,0,1970-01-01T01:01\n" +
               subtaskId + ",SUBTASK,Subtask#1-1,IN_PROGRESS,Subtask1-1 description," + epicId + ",null,null,null" + "\n";

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The subtask was not saved to a file!");
    }

    @Test
    void getTaskById_shouldSaveTaskIdInFileHistory() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = fileBackedTaskManager.createTask(task);

        fileBackedTaskManager.getTaskById(taskId);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               taskId + ",TASK,Task#1,NEW,Task1 description,null,null,null\n" +
                "\n" + taskId;

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The task id was not saved to the history in the file!");
    }

    @Test
    void getEpicById_shouldSaveEpicIdInFileHistory() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        fileBackedTaskManager.getEpicById(epicId);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,NEW,Epic1 description,null,null,null\n" +
                "\n" + epicId;

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The epic id was not saved to the history in the file!");
    }

    @Test
    void getSubtaskById_shouldSaveSubtaskIdInFileHistory() throws IOException {
        fileBackedTaskManager = createTaskManager();
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = fileBackedTaskManager.createSubtask(subtask);

        fileBackedTaskManager.getSubtaskById(subtaskId);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,IN_PROGRESS,Epic1 description,9999-12-09T23:59,0,1970-01-01T01:01\n" +
               subtaskId + ",SUBTASK,Subtask#1-1,IN_PROGRESS,Subtask1-1 description," + epicId + ",null,null,null" + "\n" +
                "\n" + subtaskId;

        String actual = Files.readString(Path.of("resources/tasksTest.csv"));

        assertEquals(expected, actual, "The subtask id was not saved to the history in the file!");
    }

    @Test
    void loadFromFile_shouldLoadFromFileTasksEpicsSubtasksAndHistory() {  // General work of the method + epic without subtasks
        FileBackedTaskManager fileBackedTaskManager3 = new FileBackedTaskManager(new File("resources/tasksTest4.csv"));
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        final int taskId1 = fileBackedTaskManager3.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager3.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = fileBackedTaskManager3.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        fileBackedTaskManager3.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        fileBackedTaskManager3.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = fileBackedTaskManager3.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        fileBackedTaskManager3.createEpic(epic2);

        fileBackedTaskManager3.getTaskById(taskId1);
        fileBackedTaskManager3.getEpicById(epicId1);
        fileBackedTaskManager3.getSubtaskById(subtaskId3);

        fileBackedTaskManager3.removeAllEpics();
        fileBackedTaskManager3.removeAllTasks();
        fileBackedTaskManager3.removeAllSubtasks();

        FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasksTest4.csv"));

        List<Task> expected1 = List.of(task1, task2);
        List<Task> actual1 = restoredManager.getAllTasks();

        assertEquals(expected1, actual1, "Tasks were not restored from the file!");

        List<Subtask> expected2 = List.of(subtask1, subtask2, subtask3);
        List<Subtask> actual2 = restoredManager.getAllSubtasks();

        assertEquals(expected2, actual2, "Subtasks were not restored from the file!");

        List<Epic> expected3 = List.of(epic1, epic2);
        List<Epic> actual3 = restoredManager.getAllEpics();

        assertEquals(expected3, actual3, "Epics were not restored from the file!");

        List<Task> expected4 = List.of(task1, epic1, subtask3);
        List<Task> actual4 = restoredManager.getHistory();

        assertEquals(expected4, actual4, "The history was not recovered from the file!");
    }


    @Test
    void loadFromFile_shouldLoadFromFileWithEmptyHistoryList() {     // Empty history list
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(new File("resources/tasksTest2.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        fileBackedTaskManager2.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager2.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = fileBackedTaskManager2.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        fileBackedTaskManager2.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        fileBackedTaskManager2.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        fileBackedTaskManager2.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        fileBackedTaskManager2.createEpic(epic2);

        fileBackedTaskManager2.removeAllTasks();
        fileBackedTaskManager2.removeAllSubtasks();
        fileBackedTaskManager2.removeAllEpics();

        FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasksTest2.csv"));

        assertTrue(restoredManager.getHistory().isEmpty(), "Task history is not empty!");
    }

    @Test
    void loadFromFile_ShouldLoadFromFileWithEmptyListOfTasksSubtasksAndEpics() throws ManagerSaveException {  // Empty task list

        new FileBackedTaskManager(new File("resources/tasksTest3.csv"));

        assertThrows(ManagerSaveException.class, () -> loadFromFile(new File("resources/tasksTest3.csv")));
    }
}