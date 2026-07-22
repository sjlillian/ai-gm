package aigm.gamestate.dto;

import aigm.gamestate.enums.RelationshipStatus;

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

    public RelationshipStatus getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(RelationshipStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

}
