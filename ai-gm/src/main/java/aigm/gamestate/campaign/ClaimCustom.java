package aigm.gamestate.campaign;

public record ClaimCustom(
    String name,
    String description,
    String perk
) implements Claim {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPerk() {
        return perk;
    }

}
