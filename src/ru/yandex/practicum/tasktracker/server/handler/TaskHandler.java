package ru.yandex.practicum.tasktracker.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.service.Managers;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    private String getPath;
    private HttpExchange httpExchange;
    private Gson gson;
    private byte[] response = new byte[0];
    private final TaskManager taskManager;
    private int responseCode = 405;

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        httpExchange = exchange;
        getPath = path;
        gson = Managers.getGson();

        try {
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        getPrioritizedTasks();
                        break;
                    }
                    if (Pattern.matches("^/tasks/task$", path)) {
                        getTasks();
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        getEpics();
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        getSubtasks();
                        break;
                    }
                    if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                        getTaskById();
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                        getSubtaskById();
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/\\d+$", path)) {
                        getSubtaskByEpic();
                        break;
                    }
                    if (Pattern.matches("^/tasks/history$", path)) {
                        getHistory();
                        break;
                    }
                    break;

                case "POST":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        createTask();
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        createEpic();
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        createSubtask();
                        break;
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        deleteAllTasks();
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        deleteAllSubtasks();
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        deleteAllEpics();
                        break;
                    }
                    if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                        deleteById();
                        break;
                    }
                    break;

                default:
                    System.out.println("Waiting for a request GET/POST/DELETE, but accepted - " + method);
            }
            exchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IOException e) {
            System.out.println("Request execution error");
        } finally {
            exchange.close();
        }
    }

    private void getPrioritizedTasks() {
        System.out.println("GET: request /tasks processed.");
        String prioritizedTasksToJson = gson.toJson(taskManager.getPrioritizedTasks());

        checkIsEmpty(prioritizedTasksToJson);
        response = prioritizedTasksToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getTasks() {
        System.out.println("GET: request /tasks/task processed");
        String tasksToJson = gson.toJson(taskManager.getAllTasks());

        checkIsEmpty(tasksToJson);
        response = tasksToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getEpics() {
        System.out.println("GET: request /tasks/epic processed");
        String epicToJson = gson.toJson(taskManager.getAllEpics());

        checkIsEmpty(epicToJson);
        response = epicToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getSubtasks() {
        System.out.println("GET: request /tasks/subtask processed");
        String subtasksToJson = gson.toJson(taskManager.getAllSubtasks());

        checkIsEmpty(subtasksToJson);
        response = subtasksToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }


    private void getHistory() {
        System.out.println("GET: request /tasks/history processed");
        String historyToJson = gson.toJson(taskManager.getHistory());

        checkIsEmpty(historyToJson);
        response = historyToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getTaskById() {
        System.out.println("GET: request /tasks/task/?id= processed");
        String pathId = getPath.replaceFirst("/tasks/task/", "");
        int id = parsePathId(pathId);
        if (checkIsIdIncorrect(id)) {
            return;
        }
        String taskByIdToJson = gson.toJson(taskManager.getTaskById(id));

        checkIsEmpty(taskByIdToJson);
        response = taskByIdToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getSubtaskById() {
        System.out.println("GET: request /tasks/subtask/?id= processed");
        String pathId = getPath.replaceFirst("/tasks/subtask/", "");
        int id = parsePathId(pathId);
        if (checkIsIdIncorrect(id)) {
            return;
        }
        String subtaskEpicToJson = gson.toJson(taskManager.getSubtaskById(id));

        checkIsEmpty(subtaskEpicToJson);
        response = subtaskEpicToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void getSubtaskByEpic() {
        System.out.println("GET: request /tasks/subtask/epic/?id= processed");
        String pathId = getPath.replaceFirst("/tasks/subtask/epic/", "");
        int id = parsePathId(pathId);
        if (checkIsIdIncorrect(id)) {
            return;
        }
        String subtaskEpicToJson = gson.toJson(taskManager.getSubtasksByEpic(id));

        checkIsEmpty(subtaskEpicToJson);
        response = subtaskEpicToJson.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        responseCode = 200;
    }

    private void createTask() throws IOException {
        System.out.println("POST: request /tasks/task processed");
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        responseCode = 200;

        if (!taskManager.getAllTasks().contains(task)) {
            taskManager.createTask(task);
            System.out.println("Task created");
        } else {
            taskManager.updateTask(task);
            System.out.println("Task updated");
        }
    }

    private void createEpic() throws IOException {
        System.out.println("POST: request /tasks/epic processed");
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        responseCode = 200;

        if (!taskManager.getAllEpics().contains(epic)) {
            taskManager.createEpic(epic);
            System.out.println("Epic created");
        } else {
            taskManager.updateEpic(epic);
            System.out.println("Epic updated");
        }
    }

    private void createSubtask() throws IOException {
        System.out.println("POST: request /tasks/subtask processed");
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        responseCode = 200;

        if (!taskManager.getAllSubtasks().contains(subtask)) {
            taskManager.createSubtask(subtask);
            System.out.println("Subtask created");
        } else {
            taskManager.updateSubtask(subtask);
            System.out.println("Subtask updated");
        }
    }

    private void deleteAllTasks() {
        System.out.println("DELETE: request /tasks/task processed");
        taskManager.removeAllTasks();
        System.out.println("All tasks removed");
        responseCode = 200;
    }

    private void deleteAllSubtasks() {
        System.out.println("DELETE: request /tasks/subtask processed");
        taskManager.removeAllSubtasks();
        System.out.println("All subtasks removed");
        responseCode = 200;
    }

    private void deleteAllEpics() {
        System.out.println("DELETE: request /tasks/epic processed");
        taskManager.removeAllEpics();
        System.out.println("All epics removed");
        responseCode = 200;
    }

    private void deleteById() {
        System.out.println("DELETE: request /tasks/task/?id= processed");
        String pathId = getPath.replaceFirst("/tasks/task/", "");
        int id = parsePathId(pathId);
        if (checkIsIdIncorrect(id)) {
            return;
        }
        if (taskManager.getAllTasks().contains(taskManager.getTaskById(id))) {
            taskManager.removeTaskById(id);
            System.out.println("Task removed");
        }
        if (taskManager.getAllEpics().contains(taskManager.getEpicById(id))) {
            taskManager.removeEpicById(id);
            System.out.println("Epic removed");
        }
        if (taskManager.getAllSubtasks().contains(taskManager.getSubtaskById(id))) {
            taskManager.removeSubtaskById(id);
            System.out.println("Subtask removed");
        }
        responseCode = 200;
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean checkIsIdIncorrect(int id) {
        if (id == -1) {
            System.out.println("Incorrect id received " + id);
            responseCode = 405;
            return true;
        } else {
            return false;
        }
    }

    private void checkIsEmpty(String str) {
        if (str.isEmpty()) {
            responseCode = 404;
        }
    }
}
