package ru.yandex.practicum.tasktracker.task;

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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }

        Subtask subtask = (Subtask) o;

        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }
}
