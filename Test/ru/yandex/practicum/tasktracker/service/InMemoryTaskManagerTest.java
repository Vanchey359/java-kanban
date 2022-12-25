package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {

    private TaskManager taskManager;


    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    protected TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void getAllTasks_shouldReturnEmptyTasksList() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "The List is not empty!");
    }

    @Test
    void getAllTasks_shouldReturnTasksList() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        taskManager.createTask(task);

        List<Task> expected = List.of(task);
        List<Task> actual = taskManager.getAllTasks();

        assertEquals(1, actual.size(), "The size of tasks does not match!");
        assertEquals(expected, actual, "Did not return the list of tasks!");
    }

    @Test
    void getAllEpics_shouldReturnEmptyEpicsList() {
        assertTrue(taskManager.getAllEpics().isEmpty(), "The List is not empty!");
    }

    @Test
    void getAllEpics_shouldReturnEpicsList() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        taskManager.createEpic(epic);

        List<Epic> expected = List.of(epic);
        List<Epic> actual = taskManager.getAllEpics();

        assertEquals(1, actual.size(), "The size of epics does not match!");
        assertEquals(expected, actual, "Did not return the list of epics!");
    }

    @Test
    void getAllSubtasks_shouldReturnEmptySubtasksList() {
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "The List is not empty!");
    }

    @Test
    void getAllSubtasks_shouldReturnSubtasksList() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.NEW);
        subtask.setEpicId(epicId);
        taskManager.createSubtask(subtask);

        List<Subtask> expected = List.of(subtask);
        List<Subtask> actual = taskManager.getAllSubtasks();

        assertEquals(1, actual.size(), "The size of subtasks does not match!");
        assertEquals(expected, actual, "Didn't return the list of subtasks!");
    }

    @Test
    void removeAllTasks_shouldRemoveTasksFromTasksList() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        taskManager.createTask(task);

        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Tasks are not deleted!");
    }

    @Test
    void removeAllTasks_shouldRemoveTasksFromHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        taskManager.getTaskById(taskId);
        taskManager.removeAllTasks();

        assertTrue(taskManager.getHistory().isEmpty(), "The task has not been removed from the history!");
    }

    @Test
    void removeAllEpics_shouldRemoveEpicsAndSubtasksFromEpicsAndSubtasksList() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.NEW);
        subtask.setEpicId(epicId);
        taskManager.createSubtask(subtask);

        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Subtasks are not deleted!");
        assertTrue(taskManager.getAllEpics().isEmpty(), "The epics are not deleted!");
    }

    @Test
    void removeAllEpics_shouldRemoveEpicsAndSubtasksFromHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.NEW);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);
        taskManager.removeAllEpics();

        assertTrue(taskManager.getHistory().isEmpty(), "Subtasks and epics are not deleted from history!");
    }

    @Test
    void removeAllSubtasks_shouldRemoveAllSubtasksFromSubtasksListAndChangeEpicStatusToNew() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        taskManager.createSubtask(subtask);

        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Subtasks are not deleted!");
        assertEquals(Status.NEW, epic.getStatus(), "The epic status has not been updated to NEW!");
        assertTrue(epic.getSubtaskIds().isEmpty(), "SubtaskIds not removed from the epic");
    }

    @Test
    void removeAllSubtasks_shouldRemoveSubtasksFromHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getSubtaskById(subtaskId);
        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getHistory().isEmpty(), "Subtasks are not deleted from the history!");
    }

    @Test
    void getTaskById_shouldReturnTaskByIdAndAddToHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(taskId), "Failed to get task!");
        assertEquals(1, taskManager.getHistory().size(), "The task has not been saved in history!");
    }

    @Test
    void getEpicById_shouldReturnEpicByIdAndAddToHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epicId), "Failed to get epic!");
        assertEquals(1, taskManager.getHistory().size(), "The epic has not been saved in history!");
    }

    @Test
    void getSubtaskById_shouldReturnSubtaskAndAddToHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId), "Failed to get subtask!");
        assertEquals(1, taskManager.getHistory().size(), "The subtask was not saved in history!");
    }

    @Test
    void createTask_shouldCreateTask() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(taskId), "Task not created!");
    }

    @Test
    void createEpic_shouldCreateEpicAndSetEpicStatusNew() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epicId), "Epic not created!");
        assertEquals(Status.NEW, epic.getStatus(), "The status of the epic is set incorrectly!");
    }

    @Test
    void createSubtask_shouldCreateSubtaskAndCalculateEpicStatus() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId), "Subtask not created!");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus(), "Epic status calculated incorrectly!");
        assertEquals(1, epic.getSubtaskIds().size(), "Subtask not attached to epic!");
    }

    @Test
    void updateTask_shouldUpdateTasksList() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        Task taskUpdate = new Task();
        taskUpdate.setTitle("Task#Update");
        taskUpdate.setDescription("TaskUpdate description");
        taskUpdate.setStatus(Status.IN_PROGRESS);
        taskUpdate.setId(taskId);
        taskManager.updateTask(taskUpdate);

        assertEquals(1, taskManager.getAllTasks().size(), "The task was not updated, but a new one was created!");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId).getStatus(), "The task was not updated - the status remained the same!");
    }

    @Test
    void updateSubtask_shouldUpdateSubtasksListAndCalculateEpicStatus() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask subtaskUpdate = new Subtask();
        subtaskUpdate.setTitle("Subtask#1-1 Update");
        subtaskUpdate.setDescription("Subtask1-1 description Update");
        subtaskUpdate.setStatus(Status.DONE);
        subtaskUpdate.setEpicId(epicId);
        subtaskUpdate.setId(subtaskId);
        taskManager.updateSubtask(subtaskUpdate);

        assertEquals(1, taskManager.getAllSubtasks().size(), "The subtask has not been updated, but a new one has been created!");
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtaskId).getStatus(), "The subtask was not updated - the status remained the same!");
        assertEquals(Status.DONE, taskManager.getEpicById(epicId).getStatus(), "Epic status not updated!");
    }

    @Test
    void updateEpic_shouldUpdateEpicsListAndCalculateEpicStatus() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Epic epicUpdate = new Epic();
        epicUpdate.setTitle("Epic#1");
        epicUpdate.setDescription("Epic1 description");
        epicUpdate.setId(epicId);
        taskManager.updateEpic(epicUpdate);

        assertEquals(1, taskManager.getAllEpics().size(), "The epic has not been updated, but a new one has been created!");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "The status of the epic was calculated incorrectly!");
    }

    @Test
    void removeTaskById_shouldRemoveTaskByIdFromTasksListAndHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        taskManager.getTaskById(taskId);
        taskManager.removeTaskById(taskId);

        assertTrue(taskManager.getHistory().isEmpty(), "The task has not been deleted from the history!");
        assertTrue(taskManager.getAllTasks().isEmpty(), "The task has not been removed from the task list!");
    }

    @Test
    void removeEpicById_shouldRemoveEpicsAndSubtasksByIdFromEpicsListAndHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        taskManager.removeEpicById(epicId);

        assertTrue(taskManager.getHistory().isEmpty(), "The epic or its subtasks are not removed from history!");
        assertTrue(taskManager.getAllEpics().isEmpty(), "The epics are not deleted!");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Subtasks are not deleted!");
    }

    @Test
    void removeSubtaskById_shouldRemoveSubtasksByIdFromSubtasksListAndEpicAndHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getSubtaskById(subtaskId);
        taskManager.removeSubtaskById(subtaskId);

        assertTrue(taskManager.getHistory().isEmpty(), "The subtask has not been deleted from the history!");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "The status of the epic has not changed - the subtask has not been deleted or the status has not been calculated");
        assertTrue(epic.getSubtaskIds().isEmpty(), "SubtaskId not removed from the epic");
    }

    @Test
    void getSubtasksByEpic_shouldReturnSubtasksByEpic() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        taskManager.createSubtask(subtask);

        List<Subtask> expected = List.of(subtask);
        List<Subtask> actual = taskManager.getSubtasksByEpic(epicId);

        assertEquals(expected, actual, "The method did not return subtasks from the epic!");
    }

    @Test
    void getHistory_shouldReturnListOfHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        assertEquals(3, taskManager.getHistory().size(), "Tasks are not saved in history!");
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty(), "History is not empty!");
    }

    @Test
    void getPrioritizedTasks_shouldReturnSetSortedTask() {
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        taskManager.createTask(task2);

        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        subtask.setStartTime(task2.getEndTime());
        subtask.setDuration(20);
        taskManager.createSubtask(subtask);

        List<Task> expected = List.of(task1, task2, subtask);
        List<Task> actual = new ArrayList<>(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void getPrioritizedTasks_shouldReturnSetSortedTaskAndTaskWithNullStartTimeMoveToEnd() { // last two elements - with null startTime
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task2");
        task2.setDescription("Task2 without startTime");
        task2.setStatus(Status.NEW);
        taskManager.createTask(task2);

        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId);
        subtask.setStartTime(task1.getEndTime().plusMinutes(1));
        subtask.setDuration(20);
        taskManager.createSubtask(subtask);

        Task task3 = new Task();
        task3.setTitle("Task#3");
        task3.setDescription("Task3 description");
        task3.setStatus(Status.NEW);
        task3.setStartTime(subtask.getEndTime().plusMinutes(1));
        task3.setDuration(10);
        taskManager.createTask(task3);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#2-1");
        subtask1.setDescription("Subtask2-1 without startTime");
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpicId(epicId);
        taskManager.createSubtask(subtask1);

        List<Task> sortedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());
        
        Task actual = sortedTasks.get(3);

        assertEquals(task2, actual, "Task without start time was not moved to the end");

        Task actual2 = sortedTasks.get(4);

        assertEquals(subtask1, actual2, "Subtask without start time was not moved to the end");
    }

    @Test
    void createTask_shouldThrowTimeCrossException() {  // Check for throwing an exception when creating a task.
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        taskManager.createTask(task2);


        Task task3 = new Task();
        task3.setTitle("Task#3");
        task3.setDescription("Task3 description");
        task3.setStatus(Status.NEW);
        task3.setStartTime(LocalDateTime.now());
        task3.setDuration(10);

        assertThrows(TimeCrossingException.class, () -> taskManager.createTask(task3));
    }

    @Test
    void updateTask_shouldThrowTimeCrossException() { // Checking for throwing an exception when updating a task.
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        taskManager.createTask(task2);


        Task task3 = new Task();
        task3.setTitle("Task#3");
        task3.setDescription("Task3 description");
        task3.setStatus(Status.NEW);
        task3.setStartTime(LocalDateTime.now());
        task3.setDuration(10);

        assertThrows(TimeCrossingException.class, () -> taskManager.updateTask(task3));
    }

    @Test
    void createSubtask_shouldThrowTimeCrossException() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpicId(epicId);
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(10);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#1-1");
        subtask2.setDescription("Subtask1-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId);
        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(10);

        assertThrows(TimeCrossingException.class, () -> taskManager.createSubtask(subtask2));
    }

    @Test
    void updateSubtask_shouldThrowTimeCrossException() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpicId(epicId);
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(10);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#1-1");
        subtask2.setDescription("Subtask1-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId);
        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(10);

        assertThrows(TimeCrossingException.class, () -> taskManager.updateSubtask(subtask2));
    }
}
