package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.server.handler.TaskHandler;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final HttpServer server;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP server running on port: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP server stopped on port: " + PORT);
    }
}
