package ru.yandex.practicum.tasktracker.task;

import ru.yandex.practicum.tasktracker.service.TaskType;

import java.util.Objects;

public class Task {

    private String title;
    private String description;
    private Integer id;
    private Status status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }

        Task task = (Task) o;

        return Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && Objects.equals(id, task.id)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    public String toCsvRow() {
        return getId() + "," + TaskType.TASK + "," + getTitle() + "," + getStatus() + "," + getDescription(); // не понял как применить в этом методе String.format - мне же надо передать в него уже готовую строку и прибавлять к ней что-то в конец или в начало
                                                                                                              //а если я каждую часть сделаю отдельной строкой и через формат буду вставлять в запятые и соединять строку это ведь будет еще хуже выглядеть
    }
}
