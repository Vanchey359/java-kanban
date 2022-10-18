public class Subtask extends Task{

    int idEpic;

    public Subtask(String title, String description, String status, int idEpic) {
        super(title, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String title, String description, int id, String status, int idEpic) {
        super(title, description, id, status);
        this.idEpic = idEpic;
    }
}
