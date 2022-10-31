import service.Managers;
import service.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import static task.Status.*;

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

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = taskManager.createEpic(epic2);


        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#1-2");
        subtask3.setDescription("Subtask1-2 description");
        subtask3.setStatus(DONE);
        subtask3.setEpicId(epicId2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);
        epic2.getSubtaskIds().add(subtaskId3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("");
        System.out.println("Проверка истории просмотра задач");
        System.out.println("");

        //Создал 10 задач, 11 - эпик, 12 - сабтаск. При корректной работе должно выдавать всего 10 элементов истории, конечными будут являться эпик и сабтаск.
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        System.out.println(taskManager.getHistory());


        System.out.println("");
        System.out.println("Меняю статусы созданных объектов");
        System.out.println("");

        Task taskOneUpdate = new Task();
        taskOneUpdate.setTitle("Task#1");
        taskOneUpdate.setDescription("Task1 update description ИЗМЕНИЛ СТАТУС НА DONE - БЫЛ NEW");
        taskOneUpdate.setId(task1.getId());
        taskOneUpdate.setStatus(DONE);
        final int updateTaskId1 = taskManager.updateTask(taskOneUpdate);

        Epic updateEpic2 = new Epic();
        updateEpic2.setTitle("Epic#2");
        updateEpic2.setDescription("Epic2 update description СТАТУС БЫЛ DONE - ДОЛЖЕН СТАТЬ NEW");
        updateEpic2.setId(epic2.getId());
        final int updateEpicId2 = taskManager.updateEpic(updateEpic2);

        Subtask subtaskThreeUpdate = new Subtask();
        subtaskThreeUpdate.setTitle("Subtask#1-2");
        subtaskThreeUpdate.setDescription("Subtask1-2 update description");
        subtaskThreeUpdate.setId(subtask3.getId());
        subtaskThreeUpdate.setStatus(NEW);
        subtaskThreeUpdate.setEpicId(updateEpicId2);
        final int updateSubtaskId3 = taskManager.updateSubtask(subtaskThreeUpdate);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("");
        System.out.println("Удаляю задачу 2 и обновленный эпик 2 (его подзадачи удаляются вместе с ним)");
        System.out.println("");

        taskManager.removeTaskById(task2.getId());
        taskManager.removeEpicById(updateEpic2.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("");
        System.out.println("Удаляю все сабтаски - Эпик 1 должен получить статус NEW");
        System.out.println("");

        taskManager.removeAllSubtasks();

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}
