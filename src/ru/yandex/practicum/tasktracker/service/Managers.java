package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.tasktracker.utils.LocalDateTimeAdapter;

import java.io.File;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/restored-manager.csv"));
    }

    public static HttpTasksManager getDefaultManager() {
        return new HttpTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        return gsonBuilder.create();
    }
}
