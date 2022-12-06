package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.task.Epic;
import ru.yandex.practicum.tasktracker.task.Status;
import ru.yandex.practicum.tasktracker.task.Subtask;
import ru.yandex.practicum.tasktracker.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public static File file;

    public FileBackedTaskManager(File file) {
        FileBackedTaskManager.file = file;
    }

    @Override
    public Task getTaskById(int taskId) {
        super.getTaskById(taskId);
        save();
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        super.getEpicById(epicId);
        save();
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        super.getSubtaskById(subtaskId);
        save();
        return subtasks.get(subtaskId);
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return super.currentId;
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return currentId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return currentId;
    }

    private String taskToString(Task task) {
        return task.getId() + "," + TaskTypes.TASK + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }

    private String epicToString(Epic epic) {
        return epic.getId() + "," + TaskTypes.EPIC + "," + epic.getTitle() + "," + epic.getStatus() + "," + epic.getDescription();
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + "," + TaskTypes.SUBTASk + "," + subtask.getTitle() + "," + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    private static String historyToString(HistoryManager manager) {
        String historyStr = String.valueOf(manager.getHistory());
        StringBuilder historyId = new StringBuilder();
        String[] historyStrArray = historyStr.split("}, ");
        for (String str : historyStrArray) {
            int index = str.indexOf("id") + 3;
           historyId.append(str.charAt(index)).append(",");
        }
        historyId.deleteCharAt(historyId.length() - 1);
        return historyId.toString();
    }

    private void save() {
            try (FileWriter fileWriter = new FileWriter(file)) {
                String firstCsv = "id,type,name,status,description,epic" + System.lineSeparator();
                fileWriter.write(firstCsv);
                for (Task task : super.getAllTasks()) {
                    fileWriter.write(taskToString(task) + System.lineSeparator());
                }
                for (Epic epic : super.getAllEpics()) {
                    fileWriter.write(epicToString(epic) + System.lineSeparator());
                }
                for (Subtask subtask : super.getAllSubtasks()) {
                    fileWriter.write(subtaskToString(subtask) + System.lineSeparator());
                }
                if (!historyManager.getHistory().isEmpty()) {
                    fileWriter.write("\n");
                    fileWriter.write(historyToString(historyManager));
                }
            } catch (IOException e) {
                    throw new ManagerSaveException(e.getMessage());
            }
    }

    private Task taskFromString(String value) {
        String[] taskValue = value.split(",");
        Task task = new Task();
        task.setTitle(taskValue[2]);         // кажется что решение с добавлением по элементу массива "циферкой" не очень хорошее, но я ничего другого не придумал
        task.setDescription(taskValue[4]);
        task.setStatus(Status.valueOf(taskValue[3]));
        task.setId(createTask(task));
        return task;
    }

    private Epic epicFromString(String value) {
        String[] epicValue = value.split(",");
        Epic epic = new Epic();
        epic.setTitle(epicValue[2]);
        epic.setDescription(epicValue[4]);
        epic.setStatus(Status.valueOf(epicValue[3]));
        epic.setId(createEpic(epic));
        return epic;
    }

    private Subtask subtaskFromString(String value) {
        String[] subtaskValue = value.split(",");
        Subtask subtask = new Subtask();
        subtask.setTitle(subtaskValue[2]);
        subtask.setDescription(subtaskValue[4]);
        subtask.setStatus(Status.valueOf(subtaskValue[3]));
        subtask.setEpicId(Integer.parseInt(subtaskValue[5]));
        subtask.setId(createSubtask(subtask));
        return subtask;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        String[] historyValueArr = value.split(",");
        for (String id : historyValueArr) {
            historyId.add(Integer.valueOf(id));
        }
        return historyId;
    }

   private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager restoredManager = new FileBackedTaskManager(file);
       try {
           String restoredStr = Files.readString(Path.of(file.toURI()));
           String[] restoredArr = restoredStr.split(System.lineSeparator());
           for (String task : restoredArr) {
               if (task.contains(String.valueOf(TaskTypes.TASK))) {
                   restoredManager.taskFromString(task);
               } else if (task.contains(String.valueOf(TaskTypes.EPIC))) {
                   restoredManager.epicFromString(task);
               } else if (task.contains(String.valueOf(TaskTypes.SUBTASk))) {     // Пришлось сделать SUBTASk с маленькой буквой потому что когда было SUBTASK при компиляции почему то срабатывал первый if на TASK и в файл сабтаски записывались с типом таск "_"
                   restoredManager.subtaskFromString(task);
               }
           }

           List<Integer> restoredHistory = historyFromString(restoredArr[restoredArr.length - 1]);
           for (Integer id : restoredHistory) {
               if (restoredManager.tasks.get(id) != null) {
                   restoredManager.historyManager.add(restoredManager.getTaskById(id));
               } else if (restoredManager.epics.get(id) != null) {
                   restoredManager.historyManager.add(restoredManager.getEpicById(id));
               } else if (restoredManager.subtasks.get(id) != null) {
                   restoredManager.historyManager.add(restoredManager.getSubtaskById(id));
               }
           }
       } catch (IOException e) {
               throw new ManagerSaveException(e.getMessage());
       }
       return restoredManager;
   }


    public static void main(String[] args) {
        FileBackedTaskManager backedManager = new FileBackedTaskManager(new File("History.csv"));

        Task task1 = new Task();
        task1.setTitle("Task#1");
        task1.setDescription("Task1 description");
        task1.setStatus(Status.NEW);
        final int taskId1 = backedManager.createTask(task1);

        Task task2 = new Task();
        task2.setTitle("Task#2");
        task2.setDescription("Task2 description");
        task2.setStatus(Status.IN_PROGRESS);
        final int taskId2 = backedManager.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setTitle("Epic#1");
        epic1.setDescription("Epic1 description");
        final int epicId1 = backedManager.createEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setTitle("Subtask#1-1");
        subtask1.setDescription("Subtask1-1 description");
        subtask1.setStatus(Status.NEW);
        subtask1.setEpicId(epicId1);
        final int subtaskId1 = backedManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setTitle("Subtask#2-1");
        subtask2.setDescription("Subtask2-1 description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setEpicId(epicId1);
        final int subtaskId2 = backedManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask();
        subtask3.setTitle("Subtask#3-1");
        subtask3.setDescription("Subtask3-1 description");
        subtask3.setStatus(Status.DONE);
        subtask3.setEpicId(epicId1);
        final int subtaskId3 = backedManager.createSubtask(subtask3);

        Epic epic2 = new Epic();
        epic2.setTitle("Epic#2");
        epic2.setDescription("Epic2 description");
        final int epicId2 = backedManager.createEpic(epic2);

        backedManager.getTaskById(taskId1);
        backedManager.getEpicById(epicId1);
        backedManager.getSubtaskById(subtaskId3);


            FileBackedTaskManager restoredManager = loadFromFile(file);

            /*
            Тесты делать даже не начинал - этот спринт дается с очень большими трудностями и мне кажется переделывать тут надо будет много :(. Если проект будет "сдан" и останется время попробую сделать и тесты.
            Я сильно извиняюсь если тут все очень плохо, я делал это 3 дня с перерывами на сон и все равно не до конца понял что от меня хотят в этом спринте и зачем я все это делал
             */
    }

}
