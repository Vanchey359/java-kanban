package ru.yandex.practicum.tasktracker.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

    public List<Integer> getSubtaskIds() {
        return Collections.unmodifiableList(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        return subtaskIds.equals(epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subtaskIds.hashCode();
        return result;
    }
}
