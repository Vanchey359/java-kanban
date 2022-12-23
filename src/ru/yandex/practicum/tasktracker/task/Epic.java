package ru.yandex.practicum.tasktracker.task;

import ru.yandex.practicum.tasktracker.service.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public List<Integer> getSubtaskIds() {
        return Collections.unmodifiableList(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task) || !super.equals(o)) {
            return false;
        }

        Epic epic = (Epic) o;

        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toCsvRow() {
        return getId() + "," + TaskType.EPIC + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration() + "," + getEndTime();
    }
}
