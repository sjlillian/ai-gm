package aigm.gamestate.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.campaign.CrewStanding.Hold;
import aigm.gamestate.player.Player;
import aigm.gamestate.score.Score;

public record Crew(
    String name,
    CrewType type,
    String lair,
    String huntingGrounds,
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
    Map<String, RelationshipStatus> factionStatuses
) {

    public static final int CREW_XP_BOXES = 8;
    public static final int COIN_CAP_WITHOUT_VAULT = 4;

    public Crew {
        members = List.copyOf(members);
        abilities = List.copyOf(abilities);
        upgrades = List.copyOf(upgrades);
        contacts = List.copyOf(contacts);
        claims = List.copyOf(claims);
        scores = List.copyOf(scores);
        clocks = List.copyOf(clocks);
        factionStatuses = Map.copyOf(factionStatuses);
        coin = Math.max(0, coin);
    }

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

    public Crew addCoin(int amount) {
        return withCoin(coin + amount);
    }

    /** Returns empty if the crew can't afford it, instead of allowing negative coin. */
    public Optional<Crew> spendCoin(int amount) {
        if (amount < 0 || coin < amount) {
            return Optional.empty();
        }
        return Optional.of(withCoin(coin - amount));
    }

    public Crew updateHeat(int delta) {
        return withHeat(heat.updateHeat(delta));
    }

    /**
     * Fill-rep advancement: weak hold becomes strong (free). Strong hold spends
     * coin equal to new tier × 8 to go up a tier (hold becomes weak). No-ops if
     * the rep track is not full, or if the crew cannot afford the coin.
     */
    public Crew tryAdvance() {
        if (crewStanding.canAdvanceHold()) {
            return withCrewStanding(crewStanding.advanceHold());
        }
        if (crewStanding.canAdvanceTier()) {
            return spendCoin(crewStanding.tierAdvancementCost())
                .map(paid -> paid.withCrewStanding(crewStanding.advanceTier()))
                .orElse(this);
        }
        return this;
    }

    public Crew reduceHold() {
        return withCrewStanding(crewStanding.reduceHold());
    }

    public Crew addRep(int amount) {
        return withCrewStanding(crewStanding.addRep(amount));
    }

    public Crew addTurf(int amount) {
        return withCrewStanding(crewStanding.addTurf(amount));
    }

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

    public Crew setFactionStatus(String factionId, RelationshipStatus status) {
        Map<String, RelationshipStatus> updated = new HashMap<>(factionStatuses);
        updated.put(factionId, status);
        return withFactionStatuses(updated);
    }

    public Crew declareWar(String factionId) {
        return setFactionStatus(factionId, RelationshipStatus.WAR);
    }

    public Crew endWar(String factionId) {
        return setFactionStatus(factionId, RelationshipStatus.HOSTILE);
    }

    public boolean isAtWar() {
        return factionStatuses.values().stream().anyMatch(RelationshipStatus::isAtWar);
    }

    private Crew withMembers(List<Player> v) {
        return new Crew(name, type, lair, huntingGrounds, v, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withCoin(int v) {
        return new Crew(name, type, lair, huntingGrounds, members, v, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withHeat(Heat v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, v, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withCrewStanding(CrewStanding v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, v, crewXP, abilities, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withCrewXP(Clock v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, v, abilities, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withAbilities(List<Ability> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, v, upgrades, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withUpgrades(List<Upgrade> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, v, contacts, claims, scores, clocks, factionStatuses);
    }

    private Crew withContacts(List<Contact> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, upgrades, v, claims, scores, clocks, factionStatuses);
    }

    private Crew withClaims(List<Claim> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, v, scores, clocks, factionStatuses);
    }

    private Crew withScores(List<Score> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, v, clocks, factionStatuses);
    }

    private Crew withClocks(List<Clock> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, v, factionStatuses);
    }

    private Crew withFactionStatuses(Map<String, RelationshipStatus> v) {
        return new Crew(name, type, lair, huntingGrounds, members, coin, heat, crewStanding, crewXP, abilities, upgrades, contacts, claims, scores, clocks, v);
    }
}
