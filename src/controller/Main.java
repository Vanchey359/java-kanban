package controller;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus("NEW");
        final int taskId1 = manager.createNewTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus("IN_PROGRESS");
        final int taskId2 = manager.createNewTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus("NEW");
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = manager.createNewSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus("IN_PROGRESS");
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = manager.createNewSubtask(subtask2);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = manager.createNewEpic(epic2);


        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#1-2");
        subtask3.setDescription("Subtask1-2 description");
        subtask3.setStatus("DONE");
        subtask3.setEpicId(epicId2);
        final int subtaskId3 = manager.createNewSubtask(subtask3);
        epic2.getSubtaskIds().add(subtaskId3);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        System.out.println("");
        System.out.println("Меняю статусы созданных объектов");
        System.out.println("");

        Task taskOneUpdate = new Task();
        taskOneUpdate.setTitle("Task#1");
        taskOneUpdate.setDescription("Task1 update description ИЗМЕНИЛ СТАТУС НА DONE - БЫЛ NEW");
        taskOneUpdate.setId(task1.getId());
        taskOneUpdate.setStatus("DONE");
        final int updateTaskId1 = manager.updateTask(taskOneUpdate);

        Epic updateEpic2 = new Epic();
        updateEpic2.setTitle("Epic#2");
        updateEpic2.setDescription("Epic2 update description СТАТУС БЫЛ DONE - ДОЛЖЕН СТАТЬ NEW");
        updateEpic2.setId(epic2.getId());
        final int updateEpicId2 = manager.updateEpic(updateEpic2);

        Subtask subtaskThreeUpdate = new Subtask();
        subtaskThreeUpdate.setTitle("Subtask#1-2");
        subtaskThreeUpdate.setDescription("Subtask1-2 update description");
        subtaskThreeUpdate.setId(subtask3.getId());
        subtaskThreeUpdate.setStatus("NEW");
        subtaskThreeUpdate.setEpicId(updateEpicId2);
        final int updateSubtaskId3 = manager.updateSubtask(subtaskThreeUpdate);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        System.out.println("");
        System.out.println("Удаляю задачу 2 и обновленный эпик 2 (его подзадачи удаляются вместе с ним)");
        System.out.println("");

        manager.removeTaskById(task2.getId());
        manager.removeEpicById(updateEpic2.getId());

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
    }
    }
