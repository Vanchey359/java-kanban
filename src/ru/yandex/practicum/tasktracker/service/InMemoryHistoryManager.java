package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        List<Task> tasksHistory = new ArrayList<>();
        for (Node node = first; node != null; node = node.next) {
            tasksHistory.add(node.task);
        }
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        nodes.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int taskId) {

        final Node node = nodes.get(taskId);
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.task = null;
    }

    private Node linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(task);
        newNode.prev = last;
        newNode.next = null;
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        return newNode;
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }
}
