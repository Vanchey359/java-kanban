package ru.yandex.practicum.tasktracker.task;

import ru.yandex.practicum.tasktracker.service.TaskType;

import java.util.Objects;

public class Subtask extends Task {

    private Integer epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task) || !super.equals(o)) {
            return false;
        }

        Subtask subtask = (Subtask) o;

        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toCsvRow() {
        return getId() + "," + TaskType.SUBTASK + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," + getEpicId();
    }
}
