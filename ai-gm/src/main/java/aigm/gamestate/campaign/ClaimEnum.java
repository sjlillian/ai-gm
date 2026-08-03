package aigm.gamestate.campaign;

public enum ClaimEnum implements Claim {
    CLAIM_1("Claim 1", "Description for Claim 1", "Perk for Claim 1"),
    CLAIM_2("Claim 2", "Description for Claim 2", "Perk for Claim 2"),
    CLAIM_3("Claim 3", "Description for Claim 3", "Perk for Claim 3");

    private final String name;
    private final String description;
    private final String perk;

    ClaimEnum(String name, String description, String perk) {
        this.name = name;
        this.description = description;
        this.perk = perk;
    }

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
