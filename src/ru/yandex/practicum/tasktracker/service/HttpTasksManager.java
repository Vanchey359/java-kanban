package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tasktracker.clients.KVTaskClient;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class HttpTasksManager extends FileBackedTaskManager {

    protected KVTaskClient client;
    private final Gson gson;

    public HttpTasksManager() {
        gson = Managers.getGson();
        client = new KVTaskClient();
    }

    public KVTaskClient getClient() {
        return client;
    }

    @Override
    public void save() {
        String prioritizedTasks = gson.toJson(getPrioritizedTasks());
        client.save("tasks", prioritizedTasks);

        String history = gson.toJson(getHistory());
        client.save("tasks/history", history);

        String tasks = gson.toJson(getTasksMap());
        client.save("tasks/task", tasks);

        String epics = gson.toJson(getEpicsMap());
        client.save("tasks/epic", epics);

        String subtasks = gson.toJson(getSubtasksMap());
        client.save("tasks/subtask", subtasks);
    }

    public void load() {
        String jsonPrioritizedTasks = getClient().load("tasks");
        Type prioritizedTaskType = new TypeToken<List<Task>>(){}.getType();
        List<Task> priorityTasks = gson.fromJson(jsonPrioritizedTasks, prioritizedTaskType);
        getPrioritizedTasks().addAll(priorityTasks);

        String gsonHistory = getClient().load("tasks/history");
        Type historyType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(gsonHistory, historyType);
        getHistory().addAll(history);

        String jsonTasks = getClient().load("tasks/task");
        Type taskType = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> tasks = gson.fromJson(jsonTasks, taskType);
        getTasksMap().putAll(tasks);

        String jsonEpics = getClient().load("tasks/epic");
        Type epicType = new TypeToken<Map<Integer, Epic>>(){}.getType();
        Map<Integer, Epic> epics = gson.fromJson(jsonEpics, epicType);
        getEpicsMap().putAll(epics);

        String jsonSubtasks = getClient().load("tasks/subtask");
        Type subtaskType = new TypeToken<Map<Integer, Subtask>>(){}.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(jsonSubtasks, subtaskType);
        getSubtasksMap().putAll(subtasks);
    }
}
