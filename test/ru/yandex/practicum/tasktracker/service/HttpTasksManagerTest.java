package ru.yandex.practicum.tasktracker.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.servers.KVServer;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class HttpTasksManagerTest extends FileBackedTaskManagerTest{

    @Test
    void shoulLoadManagerFromServer() throws IOException {
        new KVServer().start();

        TaskManager httpManager = Managers.getDefaultManager();

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(10);
        final int taskId1 = httpManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.NEW);
        task2.setStartTime(task1.getEndTime().plusMinutes(1));
        task2.setDuration(10);
        final int taskId2 = httpManager.createTask(task2);

        Epic epic = new Epic();
        epic.setTitle("Epic#1");
        epic.setDescription("Epic1 description");
        final int epicId1 = httpManager.createEpic(epic);

        Subtask subtask = new Subtask();
        subtask.setTitle("Subtask#1-1");
        subtask.setDescription("Subtask1-1 description");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpicId(epicId1);
        subtask.setStartTime(task2.getEndTime().plusMinutes(1));
        subtask.setDuration(20);
        final int subtaskId1 = httpManager.createSubtask(subtask);

        httpManager.getTaskById(taskId1);
        httpManager.getTaskById(taskId2);
        httpManager.getSubtaskById(subtaskId1);
        httpManager.getEpicById(epicId1);
        httpManager.getHistory();

        HttpTasksManager refreshedHttpManager = new HttpTasksManager();
        refreshedHttpManager.load();

        Assertions.assertEquals(httpManager.getAllTasks(), refreshedHttpManager.getAllTasks());
        Assertions.assertEquals(httpManager.getAllSubtasks(), refreshedHttpManager.getAllSubtasks());
        Assertions.assertEquals(httpManager.getAllEpics(), refreshedHttpManager.getAllEpics());
    }
}