package aigm.gamestate.dto;

import lombok.Data;

@Data
public class Claim {

    private String name;
    private String description;
    private String perk;

    public Claim() {
        // default constructor for serialization
    }

    public Claim(String name, String description, String perk) {
        this.name = name;
        this.description = description;
        this.perk = perk;
    }
}
