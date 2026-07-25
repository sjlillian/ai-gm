package aigm.gamestate.dto;

import lombok.Data;

@Data
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
}
