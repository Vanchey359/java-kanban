import java.util.ArrayList;

public class Epic extends Task{

    public ArrayList<Integer> idSubtask = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }
}
