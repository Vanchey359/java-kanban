import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        Task task1 = task(1L);
        Task task2 = task(2L);
        Task task3 = task(3L);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        Task task1 = task(1L);
        Task task2 = task(2L);
        Task task3 = task(3L);
        Task task4 = task(1L);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        List<Task> expected = List.of(task2, task3, task1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        Task task1 = task(1L);
        Task task2 = task(2L);
        Task task3 = task(3L);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove((int) 2L);

        List<Task> expected = List.of(task1, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        Task task1 = task(1L);
        Task task2 = task(2L);
        Task task3 = task(3L);
        Task task4 = task(1L);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        List<Task> expected = List.of(task2, task3, task1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual); // Идентичен методу выше - получается он не нужен? (Вопрос в том можно ли 1 методом теста закрывать сразу несколько тестируемых случаев или лучше для каждого случая писать отдельный метод как я это сделал тут?)
    }

    private static Task task(long id) {
        Task task = new Task();
        task.setId((int) id);
        return task;
    }
}
