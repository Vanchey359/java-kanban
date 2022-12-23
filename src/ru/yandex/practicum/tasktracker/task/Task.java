package ru.yandex.practicum.tasktracker.task;

import ru.yandex.practicum.tasktracker.service.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    private String title;
    private String description;
    private Integer id;
    private Status status;
    private Integer duration;
    private LocalDateTime startTime;

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        if (startTime != null && duration != null) {
             endTime = startTime.plusMinutes(duration);
        }
        return endTime;
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
        return getId() + "," + TaskType.TASK + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration() + "," + getEndTime();
    }

    @Override
    public int compareTo(Task o) {

        if (this.startTime == null) {
            return 1;
        }
        if (o.getStartTime() == null) {
            return -1;
        }
        if (this.startTime.isAfter(o.getStartTime())) {
            return 1;
        }
        return -1;
    }
}
