import service.Managers;
import service.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import static task.Status.DONE;
import static task.Status.IN_PROGRESS;
import static task.Status.NEW;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(NEW);
        final int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(IN_PROGRESS);
        final int taskId2 = taskManager.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(NEW);
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = taskManager.createEpic(epic2);

        taskManager.getTaskById(taskId1);

        taskManager.getTaskById(taskId2);

        taskManager.getEpicById(epicId1);

        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);

        taskManager.getEpicById(epicId2);
        System.out.println(taskManager.getHistory());

        System.out.println("");

        taskManager.getTaskById(taskId1); // Повторный запрос - Таск 1 должна удалиться из начала списка и встать в конец.
        System.out.println(taskManager.getHistory());

        System.out.println("");

        taskManager.getSubtaskById(subtaskId1); // Повторный запрос - Сабтаск 1 должен удалиться с 3 позиции в списке и встать в конец.
        System.out.println(taskManager.getHistory());

        System.out.println("");

        taskManager.getEpicById(epicId1); // Повторный запрос - Эпик 1 должен удалиться с 2 позиции в списке и встать в конец.
        System.out.println(taskManager.getHistory());

        System.out.println("");

        taskManager.removeTaskById(taskId2); // Удаляю Таск 2 - находился на 1 позиции в списке.
        System.out.println(taskManager.getHistory());

        System.out.println("");

        taskManager.removeEpicById(epicId1); // Удаляю Эпик 1, с ним должны удалиться все его Сабтаски.
        System.out.println(taskManager.getHistory());
    }
}
