package aigm.gamestate.dto;

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

    public String getPerk() {
        return perk;
    }

    public void setPerk(String perk) {
        this.perk = perk;
    }

}
