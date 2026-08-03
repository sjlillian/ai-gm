package aigm.gamestate.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.player.Player;
import aigm.gamestate.score.Score;

public record Crew(
    String name,
    CrewType type,
    List<Player> members,
    int coin,
    Heat heat,
    CrewStanding crewStanding,
    Clock crewXP,
    List<Ability> abilities,
    List<Upgrade> upgrades,
    List<Contact> contacts,
    List<Claim> claims,
    List<Score> scores,
    List<Clock> clocks,
    Optional<String> atWarWith
) {

    public Crew {
        members = List.copyOf(members);
        abilities = List.copyOf(abilities);
        upgrades = List.copyOf(upgrades);
        contacts = List.copyOf(contacts);
        claims = List.copyOf(claims);
        scores = List.copyOf(scores);
        clocks = List.copyOf(clocks);
    }

    // ---- members ----

    public Crew addMember(Player player) {
        List<Player> updated = new ArrayList<>(members);
        updated.add(player);
        return withMembers(updated);
    }

    public Crew removeMember(Player player) {
        List<Player> updated = new ArrayList<>(members);
        updated.remove(player);
        return withMembers(updated);
    }

    // ---- coin ----

    public Crew addCoin(int amount) {
        return withCoin(coin + amount);
    }

    /** Returns empty if the crew can't afford it, instead of allowing negative coin. */
    public Optional<Crew> spendCoin(int amount) {
        if (coin < amount) return Optional.empty();
        return Optional.of(withCoin(coin - amount));
    }

    // ---- heat / wanted level ----

    public Crew addHeat(int amount) {
        return withHeat(heat.addHeat(amount));
    }

    /** Incarceration: wanted level -1, heat cleared. The only way wanted level goes down. */
    public Crew incarcerate() {
        return withHeat(heat.clearOnIncarceration());
    }

    // ---- tier / hold advancement ----

    /**
     * Attempts to advance per the fill-rep rule: if hold is weak, it becomes strong
     * (free). If hold is already strong, spend coin to advance Tier instead. No-ops
     * if rep isn't full yet, or if strong-hold-but-can't-afford-Tier (crew just sits
     * capped until they can pay, per the rules).
     */
    public Crew tryAdvance() {
        if (!crewStanding.repFull()) {
            return this;
        }
        if (crewStanding.hold() == Hold.WEAK) {
            return withCrewStanding(crewStanding.advanceHold());
        }
        int cost = crewStanding.tierAdvancementCost();
        return spendCoin(cost)
                .map(paid -> paid.withCrewStanding(paid.crewStanding.advanceTier()))
                .orElse(this);
    }

    public Crew reduceHold() {
        return withCrewStanding(crewStanding.reduceHold());
    }

    public Crew addRep(int amount) {
        return withCrewStanding(crewStanding.addRep(amount));
    }

    // ---- crew advancement (crewXP) ----

    public boolean readyToAdvance() {
        return crewXP.isComplete();
    }

    public Crew markCrewXp(int amount) {
        return withCrewXP(crewXP.tick(amount));
    }

    public Crew advanceWithAbility(Ability newAbility) {
        return withCrewXP(crewXP.withProgress(0)).addAbility(newAbility);
    }

    public Crew advanceWithFreeUpgrades(Upgrade first, Upgrade second) {
        return withCrewXP(crewXP.withProgress(0)).addUpgrade(first).addUpgrade(second);
    }

    // ---- abilities / upgrades / contacts / claims / scores / clocks ----

    public Crew addAbility(Ability ability) {
        List<Ability> updated = new ArrayList<>(abilities);
        updated.add(ability);
        return withAbilities(updated);
    }

    public Crew addUpgrade(Upgrade upgrade) {
        List<Upgrade> updated = new ArrayList<>(upgrades);
        updated.add(upgrade);
        return withUpgrades(updated);
    }

    public Crew addContact(Contact contact) {
        List<Contact> updated = new ArrayList<>(contacts);
        updated.add(contact);
        return withContacts(updated);
    }

    public Crew addClaim(Claim claim) {
        List<Claim> updated = new ArrayList<>(claims);
        updated.add(claim);
        return withClaims(updated);
    }

    public Crew addScore(Score score) {
        List<Score> updated = new ArrayList<>(scores);
        updated.add(score);
        return withScores(updated);
    }

    public Crew addClock(Clock newClock) {
        List<Clock> updated = new ArrayList<>(clocks);
        updated.add(newClock);
        return withClocks(updated);
    }

    // ---- war status ----

    public Crew declareWar(String rivalId) {
        return withAtWarWith(Optional.of(rivalId));
    }

    public Crew endWar() {
        return withAtWarWith(Optional.empty());
    }

    public boolean isAtWar() {
        return atWarWith.isPresent();
    }

    // ---- private "with" helpers to avoid a 14-argument constructor call at every site ----

    private Crew withMembers(List<Player> v) { return new Crew(name, type, v, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withCoin(int v) { return new Crew(name, type, members, v, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withHeat(Heat v) { return new Crew(name, type, members, coin, v, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withCrewStanding(CrewStanding v) { return new Crew(name, type, members, coin, heat, v, crewXP, abilities, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withCrewXP(Clock v) { return new Crew(name, type, members, coin, heat, crewStanding, v, abilities, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withAbilities(List<Ability> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, v, upgrades, contacts, claims, scores, clocks, atWarWith); }
    private Crew withUpgrades(List<Upgrade> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, v, contacts, claims, scores, clocks, atWarWith); }
    private Crew withContacts(List<Contact> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, upgrades, v, claims, scores, clocks, atWarWith); }
    private Crew withClaims(List<Claim> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, v, scores, clocks, atWarWith); }
    private Crew withScores(List<Score> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, v, clocks, atWarWith); }
    private Crew withClocks(List<Clock> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, v, atWarWith); }
    private Crew withAtWarWith(Optional<String> v) { return new Crew(name, type, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, v); }
}