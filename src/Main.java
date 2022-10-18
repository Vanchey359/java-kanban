public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Task#1", "Task1 description", "NEW");
        final int taskId1 = manager.newTask(task1);

        Task task2 = new Task("Task#2", "Task2 description", "IN_PROGRESS");
        final int taskId2 = manager.newTask(task2);

        Epic epic1 = new Epic("Epic#1", "Epic1 description");
        final int epicId1 = manager.newEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask#1-1", "Subtask1-1 description", "NEW", epicId1);
        final int subtaskId1 = manager.newSubtask(subtask1);
        epic1.idSubtask.add(subtaskId1);

        Subtask subtask2 = new Subtask("Subtask#2-1", "Subtask2-1 description", "IN_PROGRESS", epicId1);
        final int subtaskId2 = manager.newSubtask(subtask2);
        epic1.idSubtask.add(subtaskId2);

        Epic epic2 = new Epic("Epic#2", "Epic2 description");
        final int epicId2 = manager.newEpic(epic2);

        Subtask subtask3 = new Subtask("Subtask#1-2", "Subtask1-2 description", "DONE", epicId2);
        final int subtaskId3 = manager.newSubtask(subtask3);
        epic2.idSubtask.add(subtaskId3);

        manager.statusEpic(epic1);
        manager.statusEpic(epic2);

        manager.getAllTask();
        manager.getAllEpic();
        manager.getAllSubtask();

        System.out.println("Меняю статусы созданных объектов");

        Task taskOneUpdate = new Task("Task#1", "Task1 update description", task1.getId(), "DONE");
        final int updateTaskId1 = manager.updateTask(taskOneUpdate);

        Epic updateEpic2 = new Epic("Epic#2", "Epic2 update description", epic2.getId());
        final int updateEpicId2 = manager.updateEpic(updateEpic2);

        Subtask subtaskThreeUpdate = new Subtask("Subtask#1-2", "Subtask1-2 update description", subtask3.getId(), "NEW", updateEpicId2);
        final int updateSubtaskId3 = manager.updateSubtask(subtaskThreeUpdate);

        manager.statusEpic(updateEpic2);

        manager.getAllTask();
        manager.getAllEpic();
        manager.getAllSubtask();

        manager.removeTaskById(task2.getId());
        manager.removeEpicById(updateEpic2.getId());
        manager.removeSubtaskById(subtaskThreeUpdate.getId());

        System.out.println("Удаляю задачу 2 и обновленный эпик 2 и подзадачу к эпику 2");

        manager.getAllTask();
        manager.getAllEpic();
        manager.getAllSubtask();
    }
}
