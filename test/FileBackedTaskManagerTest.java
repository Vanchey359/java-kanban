import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.service.FileBackedTaskManager;
import ru.yandex.practicum.tasktracker.service.ManagerSaveException;
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


public class FileBackedTaskManagerTest {

    private final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("resources/tasksTest.csv"));

    //// Так как основная работа методов наследуется от InMemoryTaskManager тут как я понимаю мне надо тестировать именно то что добавилось к этим методам в FileBackedTaskManager.
    //// В ТЗ написано про абстрактный класс, но я не понял зачем он нужен и что с ним делать, решил реализовать через 2 тестовых класса - один для основной логики InMemoryTaskManagerTest второй для дополнительной логики (этот)

    @Test
    void createTask_shouldSaveTaskInFile() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(10);
        task.setEndTime(task.getEndTime());
        final int taskId = fileBackedTaskManager.createTask(task);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
                taskId + ",TASK,Task#1,NEW,Task1 description," + task.getStartTime() + "," + task.getDuration() + "," + task.getEndTime() + "\n";
        String actual = null;

        try {
           actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Задача не сохранилась в файл!");
    }

    @Test
    void createEpic_shouldSaveEpicInFile() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,NEW,Epic1 description,null,null,null" + "\n";
        String actual = null;

        try {
            actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Эпик не сохранился в файл!");
    }

    @Test
    void createSubtask_shouldSaveSubtaskInFile() {
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
        String actual = null;

        try {
            actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Подзадача не сохранилась в файл!");
    }

    @Test
    void getTaskById_shouldSaveTaskIdInFileHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = fileBackedTaskManager.createTask(task);

        fileBackedTaskManager.getTaskById(taskId);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               taskId + ",TASK,Task#1,NEW,Task1 description,null,null,null\n" +
                "\n" + taskId;
        String actual = null;

        try {
            actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Id задачи не сохранился в историю в файле!");
    }

    @Test
    void getEpicById_shouldSaveEpicIdInFileHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = fileBackedTaskManager.createEpic(epic);

        fileBackedTaskManager.getEpicById(epicId);

        String expected = "id,type,name,status,description,epic,startTime,duration,endTime\n" +
               epicId + ",EPIC,Epic#1,NEW,Epic1 description,null,null,null\n" +
                "\n" + epicId;
        String actual = null;

        try {
            actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Id эпика не сохранился в историю в файле!");
    }

    @Test
    void getSubtaskById_shouldSaveSubtaskIdInFileHistory() {
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
        String actual = null;

        try {
            actual = Files.readString(Path.of("resources/tasksTest.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual, "Id подзадачи не сохранился в историю в файле!");
    }

    @Test
    void loadFromFile_shouldLoadFromFileTasksEpicsSubtasksAndHistory() {  /// Общая работа метода + эпик без подзадач
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        final int taskId1 = fileBackedTaskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        final int taskId2 = fileBackedTaskManager.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = fileBackedTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = fileBackedTaskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = fileBackedTaskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = fileBackedTaskManager.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = fileBackedTaskManager.createEpic(epic2);

        fileBackedTaskManager.getTaskById(taskId1);
        fileBackedTaskManager.getEpicById(epicId1);
        fileBackedTaskManager.getSubtaskById(subtaskId3);

        fileBackedTaskManager.removeAllEpics();
        fileBackedTaskManager.removeAllTasks();
        fileBackedTaskManager.removeAllSubtasks();

        FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasksTest.csv"));

        List<Task> expected1 = List.of(task1, task2);
        List<Task> actual1 = restoredManager.getAllTasks();

        assertEquals(expected1, actual1, "Задачи не восстановились из файла!");

        List<Subtask> expected2 = List.of(subtask1, subtask2, subtask3);
        List<Subtask> actual2 = restoredManager.getAllSubtasks();

        assertEquals(expected2, actual2, "Подзадачи не восстановились из файла!");

        List<Epic> expected3 = List.of(epic1, epic2);
        List<Epic> actual3 = restoredManager.getAllEpics();

        assertEquals(expected3, actual3, "Эпики не восстановились из файла!");

        List<Task> expected4 = List.of(task1, epic1, subtask3);
        List<Task> actual4 = restoredManager.getHistory();

        assertEquals(expected4, actual4, "История не восстановилась из файла!");

    }


    @Test
    void loadFromFile_shouldLoadFromFileWithEmptyHistoryList() {     //// Пустой список истории

        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(new File("resources/tasksTest2.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        final int taskId1 = fileBackedTaskManager2.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        final int taskId2 = fileBackedTaskManager2.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = fileBackedTaskManager2.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = fileBackedTaskManager2.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = fileBackedTaskManager2.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = fileBackedTaskManager2.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = fileBackedTaskManager2.createEpic(epic2);

        fileBackedTaskManager2.removeAllTasks();
        fileBackedTaskManager2.removeAllSubtasks();
        fileBackedTaskManager2.removeAllEpics();

        FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasksTest2.csv"));

        assertTrue(restoredManager.getHistory().isEmpty(), "История задач не пустая!");
    }

    @Test
    void loadFromFile_ShouldLoadFromFileWithEmptyListOfTasksSubtasksAndEpics() throws ManagerSaveException {  /// Пустой список задач

        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(new File("resources/tasksTest3.csv"));

        Throwable thrown = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager restoredManager = loadFromFile(new File("resources/tasksTest3.csv"));
        });
    }
}
