import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.service.TimeCrossingException;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {

    private final TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void getAllTasks_shouldReturnEmptyTasksList() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Лист не пустой!");
    }

    @Test
    void getAllTasks_shouldReturnTasksList() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        List<Task> expected = List.of(task);
        List<Task> actual = taskManager.getAllTasks();

        assertEquals(1, actual.size(), "Количество задач не совпадает!");
        assertEquals(expected, actual, "Не вернул список задач!");
    }

    @Test
    void getAllEpics_shouldReturnEmptyEpicsList() {
        assertTrue(taskManager.getAllEpics().isEmpty(), "Лист не пустой!");
    }

    @Test
    void getAllEpics_shouldReturnEpicsList() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        List<Epic> expected = List.of(epic);
        List<Epic> actual = taskManager.getAllEpics();

        assertEquals(1, actual.size(), "Количество эпиков не совпадают!");
        assertEquals(expected, actual, "Не вернул список эпиков!");
    }

    @Test
    void getAllSubtasks_shouldReturnEmptySubtasksList() {
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Лист не пустой!");
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
        final int subtaskId = taskManager.createSubtask(subtask);

        List<Subtask> expected = List.of(subtask);
        List<Subtask> actual = taskManager.getAllSubtasks();

        assertEquals(1, actual.size(), "Количество подзадач не совпадает!");
        assertEquals(expected, actual, "Не вернул список подзадач!");
    }

    @Test
    void removeAllTasks_shouldRemoveTasksFromTasksList() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Задача не была удалена из истории!");
    }

    @Test
    void removeAllEpics_shouldRemoveEpicsFromEpicsList() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалились!");
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
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалились!");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалились!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Подзадачи и эпики не удалились из истории!");
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
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалились!");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика не обновился на NEW!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Подзадачи не удалились из истории!");
    }

    @Test
    void getTaskById_shouldReturnTaskByIdAndAddToHistory() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(taskId), "Задачу получить не удалось!!");
        assertEquals(1, taskManager.getHistory().size(), "Задача не сохранилась в истории!");
    }

    @Test
    void getEpicById_shouldReturnEpicByIdAndAddToHistory() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epicId), "Эпик получить не удалось!");
        assertEquals(1, taskManager.getHistory().size(), "Эпик не сохранился в истории!");
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

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId), "Подзадачу получить не удалось!");
        assertEquals(1, taskManager.getHistory().size(), "Подзадача не сохранилась в истории!");
    }

    @Test
    void createTask_shouldCreateTask() {
        Task task = new Task();
        task.setTitle("Task#1");
        task.setDescription("Task1 description");
        task.setStatus(Status.NEW);
        final int taskId = taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(taskId), "Задача не создалась!");
    }

    @Test
    void createEpic_shouldCreateEpicAndSetEpicStatusNew() {
        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epicId), "Эпик не создался!");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика задан не верно!");
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

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId), "Подзадача не создалась!");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus(), "Статус эпика рассчитан не верно!");
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
        final int updateTaskId = taskManager.updateTask(taskUpdate);

        assertEquals(1, taskManager.getAllTasks().size(), "Задача не обновилась, а создалась новая!");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId).getStatus(), "Задача не обновилась - статус остался прежним!");
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
        final int updateSubtaskId = taskManager.updateSubtask(subtaskUpdate);

        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не обновилась, а создалась новая!");
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtaskId).getStatus(), "Подзадача не обновилась - статус остался прежним!");
        assertEquals(Status.DONE, taskManager.getEpicById(epicId).getStatus(), "Статус эпика не обновился!");
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
        final int updateEpicId = taskManager.updateEpic(epicUpdate);

        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не обновился, а создался новый!");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "Статус эпика рассчитался не верно!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Задача из истории не удалилась!");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача из списка задач не удалилась!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Эпик или его подзадачи не удалились из истории!");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалились!");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалились!");
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

        assertTrue(taskManager.getHistory().isEmpty(), "Подзадача не удалилась из истории!");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "Статус эпика не изменился - подзадача не удалилась или статус не был рассчитан");
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
        final int subtaskId = taskManager.createSubtask(subtask);

        List<Subtask> expected = List.of(subtask);
        List<Subtask> actual = taskManager.getSubtasksByEpic(epicId);

        assertEquals(expected, actual, "Метод не вернул подзадачи из эпика!");
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

        assertEquals(3, taskManager.getHistory().size(), "Задачи не сохранились в историю!");
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty(), "История не пустая!");
    }

    @Test
    void getPrioritizedTasks_shouldReturnSetSortedTask() {
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        task1.setEndTime(task1.getEndTime());
        final int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        task2.setEndTime(task2.getEndTime());
        final int taskId2 = taskManager.createTask(task2);

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
        subtask.setEndTime(subtask.getEndTime());
        final int subtaskId = taskManager.createSubtask(subtask);

        Task task3 = new Task();
        task3.setTitle("Task#1");
        task3.setDescription("Task1 description");
        task3.setStatus(Status.NEW);
        final int taskId3 = taskManager.createTask(task3);

        List<Task> expected = List.of(task1, task2, subtask, task3);
        List<Task> actual = new ArrayList<>(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void createTask_shouldThrowTimeCrossException() {  /// Проверка на выброс исключения при создании задачи.
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        task1.setEndTime(task1.getEndTime());
        final int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        task2.setEndTime(task2.getEndTime());
        final int taskId2 = taskManager.createTask(task2);


        Task task3 = new Task();
        task3.setTitle("Task#3");
        task3.setDescription("Task3 description");
        task3.setStatus(Status.NEW);
        task3.setStartTime(LocalDateTime.now());
        task3.setDuration(10);
        task3.setEndTime(task3.getEndTime());

        assertThrows(TimeCrossingException.class, () -> {
            final int taskId3 = taskManager.createTask(task3);
        });
    }

    @Test
    void updateTask_shouldThrowTimeCrossException() { // Проверка на выброс исключения при апдейте задачи.
        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        task1.setEndTime(task1.getEndTime());
        final int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        task2.setEndTime(task2.getEndTime());
        final int taskId2 = taskManager.createTask(task2);


        Task task3 = new Task();
        task3.setTitle("Task#3");
        task3.setDescription("Task3 description");
        task3.setStatus(Status.NEW);
        task3.setStartTime(LocalDateTime.now());
        task3.setDuration(10);
        task3.setEndTime(task3.getEndTime());

        assertThrows(TimeCrossingException.class, () -> {
            final int updateTaskId3 = taskManager.updateTask(task3);
        });
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
        subtask1.setEndTime(subtask1.getEndTime());
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#1-1");
        subtask2.setDescription("Subtask1-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId);
        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(10);
        subtask2.setEndTime(subtask1.getEndTime());

        assertThrows(TimeCrossingException.class, () -> {
            final int subtaskId2 = taskManager.createSubtask(subtask2);
        });
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
        subtask1.setEndTime(subtask1.getEndTime());
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#1-1");
        subtask2.setDescription("Subtask1-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId);
        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(10);
        subtask2.setEndTime(subtask1.getEndTime());

        assertThrows(TimeCrossingException.class, () -> {
            final int updateSubtaskId2 = taskManager.updateSubtask(subtask2);
        });
    }

    //// Писать тесты для приватных методов же не надо? Например CalculateEpicStatus и так тестируется во всех "значениях" в других моих тестах, nextId тоже.
}
