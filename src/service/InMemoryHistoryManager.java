package service;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    public static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        for (Node x : nodeMap.values()) {
            if (x.task.getId() == task.getId()) {
                removeNode(task.getId());
            }
        }
        nodeMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksHistory = new ArrayList<>();
        for (Node x = first; x != null; x = x.next) {
            tasksHistory.add(x.task);
        }
        return tasksHistory;
    }

    private Node linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(task, last, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        return newNode;
    }

    private void removeNode(int id) {

        final Node x = nodeMap.get(id);
        final Node next = x.next;
        final Node prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.task = null;
    }
}
