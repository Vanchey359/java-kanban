package ru.yandex.practicum.tasktracker.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.service.Managers;
import ru.yandex.practicum.tasktracker.service.TaskManager;
import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;
    private byte[] response = new byte[0];
    private static final int PORT = 8080;
    private HttpExchange httpExchange;
    private int responseCode = 405;
    private String getPath;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            httpExchange = exchange;
            getPath = path;

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

                    case "POST" :
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
                        System.out.println("Ожидал запрос GET/POST/DELETE, а принял - " + method);
                }
                exchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            } catch (IOException e) {
                System.out.println("Ошибка выполнения запроса");
            } finally {
                exchange.close();
            }
        }


        private void getPrioritizedTasks() {
            System.out.println("GET: запрос /tasks обрабатывается.");
            String prioritizedTasksToJson = gson.toJson(taskManager.getPrioritizedTasks());

            if (!prioritizedTasksToJson.isEmpty()) {
                response = prioritizedTasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getTasks() {
            System.out.println("GET: запрос /tasks/task обрабатывается");
            String tasksToJson = gson.toJson(taskManager.getAllTasks());

            if (!tasksToJson.isEmpty()) {
                response = tasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getEpics() {
            System.out.println("GET: запрос /tasks/epic обрабатывается");
            String epicToJson = gson.toJson(taskManager.getAllEpics());

            if (!epicToJson.isEmpty()) {
                response = epicToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getSubtasks() {
            System.out.println("GET: запрос /tasks/subtask обрабатывается");
            String subtasksToJson = gson.toJson(taskManager.getAllSubtasks());

            if (!subtasksToJson.isEmpty()) {
                response = subtasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }


        private void getHistory() {
            System.out.println("GET: запрос /tasks/history обрабатывается");
            String historyToJson = gson.toJson(taskManager.getHistory());

            if (!historyToJson.isEmpty()) {
                response = historyToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getTaskById() {
            System.out.println("GET: запрос /tasks/task/?id= обрабатывается");
            String pathId = getPath.replaceFirst("/tasks/task/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                System.out.println("Получен некорректный id " + id);
                responseCode = 405;
                return;
            }
            String taskByIdToJson = gson.toJson(taskManager.getTaskById(id));

            if (!taskByIdToJson.isEmpty()) {
                response = taskByIdToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getSubtaskById() {
            System.out.println("GET: запрос /tasks/subtask/?id= обрабатывается");
            String pathId = getPath.replaceFirst("/tasks/subtask/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                System.out.println("Получен некорректный id " + id);
                responseCode = 405;
                return;
            }
            String subtaskEpicToJson = gson.toJson(taskManager.getSubtaskById(id));

            if (!subtaskEpicToJson.isEmpty()) {
                response = subtaskEpicToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void getSubtaskByEpic() {
            System.out.println("GET: запрос /tasks/subtask/epic/?id= обрабатывается");
            String pathId = getPath.replaceFirst("/tasks/subtask/epic/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                System.out.println("Получен некорректный id " + id);
                responseCode = 405;
                return;
            }
            String subtaskEpicToJson = gson.toJson(taskManager.getSubtasksByEpic(id));

            if (!subtaskEpicToJson.isEmpty()) {
                response = subtaskEpicToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else {
                responseCode = 404;
            }
        }

        private void createTask() throws IOException {
            System.out.println("POST: запрос /tasks/task обрабатывается");
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            responseCode = 200;

            if (!taskManager.getAllTasks().contains(task)) {
                taskManager.createTask(task);
                System.out.println("Задача создана");
            } else {
                taskManager.updateTask(task);
                System.out.println("Задача обновлена");
            }
        }

        private void createEpic() throws IOException {
            System.out.println("POST: запрос /tasks/epic обрабатывается");
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            responseCode = 200;

            if (!taskManager.getAllEpics().contains(epic)) {
                taskManager.createEpic(epic);
                System.out.println("Эпик создан");
            } else {
                taskManager.updateEpic(epic);
                System.out.println("Эпик обновлен");
            }
        }

        private void createSubtask() throws IOException {
            System.out.println("POST: запрос /tasks/subtask обрабатывается");
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            responseCode = 200;

            if (!taskManager.getAllSubtasks().contains(subtask)) {
                taskManager.createSubtask(subtask);
                System.out.println("Подзадача создана");
            } else {
                taskManager.updateSubtask(subtask);
                System.out.println("Подзадача обновлена");
            }
        }

        private void deleteAllTasks() {
            System.out.println("DELETE: запрос /tasks/task обрабатывается");
            taskManager.removeAllTasks();
            System.out.println("Все задачи удалены");
            responseCode = 200;
        }

        private void deleteAllSubtasks() {
            System.out.println("DELETE: запрос /tasks/subtask обрабатывается");
            taskManager.removeAllSubtasks();
            System.out.println("Все подзадачи удалены");
            responseCode = 200;
        }

        private void deleteAllEpics() {
            System.out.println("DELETE: запрос /tasks/epic обрабатывается");
            taskManager.removeAllEpics();
            System.out.println("Все эпики удалены");
            responseCode = 200;
        }

        private void deleteById() {
            System.out.println("DELETE: запрос /tasks/task/?id= обрабатывается");
            String pathId = getPath.replaceFirst("/tasks/task/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                System.out.println("Получен некорректный id " + id);
                responseCode = 405;
                return;
            }
            if (taskManager.getAllTasks().contains(taskManager.getTaskById(id))) {
                taskManager.removeTaskById(id);
                System.out.println("Задача удалена");
            }
            if (taskManager.getAllEpics().contains(taskManager.getEpicById(id))) {
                taskManager.removeEpicById(id);
                System.out.println("Эпик удален");
            }
            if (taskManager.getAllSubtasks().contains(taskManager.getSubtaskById(id))) {
                taskManager.removeSubtaskById(id);
                System.out.println("Подзадача удалена");
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
    }
}
