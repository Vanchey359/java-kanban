import java.util.HashMap;

public class Manager {

    private int taskId = 0;
    private int subtaskId = 0;
    private int epicId = 0;

    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    public int newId(int id) {
        id += 1;
        return id;
    }

    public void getAllTask() {
        for (int i = 1; i < taskMap.size() + 1; i++) {
            System.out.println(taskMap.get(i));
        }
    }

    public void getAllEpic() {
        for (int i = 1; i < epicMap.size() + 1; i++) {
            System.out.println(epicMap.get(i));
        }
    }

    public void getAllSubtask() {
        for (int i = 1; i < subtaskMap.size() + 1; i++) {
            System.out.println(subtaskMap.get(i));
        }
    }

    public void removeAllTask() {
        taskMap.clear();
    }

    public void removeAllEpic() {
        epicMap.clear();
    }

    public void removeAllSubtask() {
        subtaskMap.clear();
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskMap.get(id);
    }

    public int newTask(Task task) {
        taskId = newId(taskId);
        task.setId(taskId);
        taskMap.put(taskId, task);
        return taskId;
    }

    public int newEpic(Epic epic) {
        epicId = newId(epicId);
        epic.setId(epicId);
        epicMap.put(epicId, epic);
        return epicId;
    }

    public int newSubtask(Subtask subtask) {
        subtaskId = newId(subtaskId);
        subtask.setId(subtaskId);
        subtaskMap.put(subtaskId, subtask);
        return subtaskId;
    }

    public int updateTask(Task task) {
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    public int updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        return subtask.getId();
    }

    public int updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    public void removeTaskById(int id) {
        taskMap.remove(id);
    }

    public void removeEpicById(int id) {
        epicMap.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtaskMap.remove(id);
    }

    public void getSubtaskByEpic(Epic epic) {
        for (int i = 0; i < epic.idSubtask.size(); i++) {
            System.out.println(subtaskMap.get(epic.idSubtask.get(i)));
        }
    }

    public void statusEpic(Epic epic) {
        if (epic.idSubtask.isEmpty()) {
            epic.setStatus("NEW");
        } else {
            for (int i = 0; i < epic.idSubtask.size(); i++) {
                String subtaskStatus = subtaskMap.get(epic.idSubtask.get(i)).getStatus();
                String epicStatus = epic.getStatus();
                if (subtaskStatus.equals("NEW") && ((epicStatus == null || !epicStatus.equals("DONE")) && (epicStatus == null || !epicStatus.equals("IN_PROGRESS")))) {
                    epic.setStatus("NEW");
                } else if (subtaskStatus.equals("DONE") && ((epicStatus == null || !epicStatus.equals("NEW")) && (epicStatus == null || !epicStatus.equals("IN_PROGRESS")))) {
                    epic.setStatus("DONE");
                } else {
                    epic.setStatus("IN_PROGRESS");
                }
            }
        }
    }
}