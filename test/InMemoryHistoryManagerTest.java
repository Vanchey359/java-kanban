import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.service.HistoryManager;
import ru.yandex.practicum.tasktracker.service.InMemoryHistoryManager;
import ru.yandex.practicum.tasktracker.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая!");
    }

    @Test
    void getHistory_shouldReturnHistory() {
        Task task1 = task(1);
        Task task2 = task(2);
        Task task3 = task(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "История не выводится!");
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        Task task1 = task(1);
        Task task2 = task(2);
        Task task3 = task(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "История не сохраняется!");
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        Task task1 = task(1);
        Task task2 = task(2);
        Task task3 = task(3);
        Task task4 = task(1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        List<Task> expected = List.of(task2, task3, task1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "История содержит дубликаты!");
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        Task task1 = task(1);
        Task task2 = task(2);
        Task task3 = task(3);
        Task task4 = task(4);
        Task task5 = task(5);
        Task task6 = task(6);
        Task task7 = task(7);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);

        historyManager.remove(1);
        historyManager.remove(4);
        historyManager.remove(7);

        List<Task> expected = List.of(task2, task3, task5, task6);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Задачи не удаляются из истории!");
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        Task task1 = task(1);
        Task task2 = task(2);
        Task task3 = task(3);
        Task task4 = task(1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        List<Task> expected = List.of(task2, task3, task1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Задача не перемещается в конец истории!");
    }

    private static Task task(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }
}
