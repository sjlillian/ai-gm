package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Crew implements Serializable{

    private String name;
    private List<Player> members;

    public Crew(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public List<Player> getMembers() {
        return members;
    }
}
