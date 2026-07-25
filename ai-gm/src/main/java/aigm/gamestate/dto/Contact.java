package aigm.gamestate.dto;

import aigm.gamestate.enums.RelationshipStatus;
import lombok.Data;

@Data
public class Contact {

    private String name;
    private String description;
    private RelationshipStatus relationshipStatus;

    public Contact() {
        // default constructor for serialization
    }

    public Contact(String name, String description, RelationshipStatus relationshipStatus) {
        this.name = name;
        this.description = description;
        this.relationshipStatus = relationshipStatus;
    }
}
