package ru.yandex.practicum.tasktracker.service;

public class TimeCrossingException extends RuntimeException{

    public TimeCrossingException(String message) {
        super(message);
    }
}
