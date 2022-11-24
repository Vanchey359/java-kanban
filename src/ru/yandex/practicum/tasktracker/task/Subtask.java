package ru.yandex.practicum.tasktracker.task;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Subtask subtask = (Subtask) o;

        return epicId.equals(subtask.epicId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + epicId.hashCode();
        return result;
    }
}
