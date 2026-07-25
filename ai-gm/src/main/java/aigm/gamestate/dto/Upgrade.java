package aigm.gamestate.dto;

import lombok.Data;

@Data
public class Upgrade {

    private String name;
    private String description;
    private int cost;

    public Upgrade() {
        // default constructor for serialization
    }

    public Upgrade(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
    }
}
