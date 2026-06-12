package aigm.gamestate.dto;

public class Item {

    private String name;
    private String description;
    private boolean fine;

    public Item() {
        // default constructor for serialization
    }

    public Item(String name, String description, boolean fine) {
        this.name = name;
        this.description = description;
        this.fine = fine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFine() {
        return fine;
    }

    public void setFine(boolean fine) {
        this.fine = fine;
    }

}
