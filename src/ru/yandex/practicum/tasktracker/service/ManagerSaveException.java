package ru.yandex.practicum.tasktracker.service;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(final String message, Throwable clause) {
        super(message, clause);
    }
}
