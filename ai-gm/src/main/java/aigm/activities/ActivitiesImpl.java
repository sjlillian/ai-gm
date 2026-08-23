package aigm.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import aigm.gamestate.DiceRoll;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.Heat;
import aigm.gamestate.player.Action;

/**
 * Dice pools and Core Rulebook table lookups. Adjudication/narration are
 * placeholders until an LLM client is wired (Temporal AI activity pattern).
 */
public class ActivitiesImpl implements Activities {

    @Override
    public DiceRoll rollAction(int actionRating, boolean pushYourself, boolean assisted) {
        int dice = Math.max(0, actionRating);
        if (pushYourself) {
            dice += 1;
        }
        if (assisted) {
            dice += 1;
        }
        return rollPool(dice);
    }

    @Override
    public DiceRoll rollFortune(int dice) {
        return rollPool(Math.max(0, dice));
    }

    @Override
    public DiceRoll rollResistance(int attributeRating) {
        return rollPool(Math.max(0, attributeRating));
    }

    @Override
    public DiceRoll rollEngagement(int dice) {
        return rollPool(Math.max(0, dice));
    }

    @Override
    public DiceRoll rollVice(int viceRating) {
        return rollPool(Math.max(0, viceRating));
    }

    @Override
    public PayoffResult determinePayoff(int targetTier, int crewTier, boolean atWar) {
        int tier = Math.max(0, Math.min(targetTier, 5));
        int coin = 2 * (tier + 1);
        int rep = atWar ? 0 : Math.max(0, tier - Math.max(0, crewTier));
        String notes = atWar
            ? "At war: no rep from this score."
            : "Payoff for a Tier " + tier + " target.";
        return new PayoffResult(coin, rep, notes);
    }

    @Override
    public HeatResult determineHeat(HeatContext context) {
        int heat = Math.max(0, context.baseHeat());
        StringBuilder notes = new StringBuilder("Heat " + heat);
        if (context.highProfile()) {
            heat += 1;
            notes.append("; +1 high-profile");
        }
        if (context.killing()) {
            heat += 2;
            notes.append("; +2 killing");
        }
        if (context.hostileTurf()) {
            heat += 1;
            notes.append("; +1 hostile turf");
        }
        if (context.wellConnectedTarget()) {
            heat += 1;
            notes.append("; +1 well-connected target");
        }
        heat += Math.max(0, context.extra());
        if (context.extra() > 0) {
            notes.append("; +").append(context.extra()).append(" extra");
        }
        return new HeatResult(heat, notes.toString());
    }

    @Override
    public EntanglementResult rollEntanglement(Heat.WantedLevel wantedLevel, int heat) {
        // Wanted level = dice pool. Heat = which table column (p. 150).
        int dice = wantedLevel == null ? 0 : wantedLevel.ordinal();
        DiceRoll roll = rollPool(dice);
        return entanglementFor(Math.max(0, heat), roll.highest());
    }

    @Override
    public AcquireAssetResult acquireAsset(int crewTier, String assetDescription) {
        DiceRoll roll = rollPool(Math.max(0, crewTier) + 1);
        int quality = roll.isCritical() ? Math.max(0, crewTier) + 2
            : roll.isFullSuccess() ? Math.max(0, crewTier) + 1
            : roll.isPartialSuccess() ? Math.max(0, crewTier)
            : Math.max(0, crewTier - 1);
        return new AcquireAssetResult(
            quality,
            "Acquired '" + assetDescription + "' at quality " + quality + " (roll " + roll.highest() + ")."
        );
    }

    @Override
    public RecoveryRollResult recover(int treatmentQuality) {
        DiceRoll roll = rollPool(Math.max(0, treatmentQuality));
        int segments = roll.isCritical() ? 3
            : roll.isFullSuccess() ? 2
            : roll.isPartialSuccess() ? 1
            : 0;
        return new RecoveryRollResult(segments, "Recovery fortune: " + roll.highest() + " → " + segments + " ticks.");
    }

    @Override
    public DiceRoll reduceHeat(int dice) {
        return rollPool(Math.max(0, dice));
    }

    @Override
    public Adjudication adjudicateAction(String situation, String approach, Action chosenAction) {
        // Placeholder until LLM GM is wired. Defaults match a typical score beat.
        return new Adjudication(
            chosenAction,
            Position.RISKY,
            Effect.STANDARD,
            "Stub adjudication for: " + approach + " using " + chosenAction
                + " given '" + situation + "'. Replace with LLM GM.",
            List.of("Complication", "Harm", "Clock advances", "Worse position")
        );
    }

    @Override
    public String narrate(String situation, String mechanicalOutcome) {
        return situation + " → " + mechanicalOutcome;
    }

    /** BitD zero-rating rule: roll two dice, keep the lower. */
    DiceRoll rollPool(int dice) {
        boolean zeroDice = dice <= 0;
        int count = zeroDice ? 2 : dice;
        List<Integer> results = new ArrayList<>(count);
        int highest = 0;
        int lowest = 7;
        int sixes = 0;
        for (int i = 0; i < count; i++) {
            int face = ThreadLocalRandom.current().nextInt(1, 7);
            results.add(face);
            highest = Math.max(highest, face);
            lowest = Math.min(lowest, face);
            if (face == 6) {
                sixes++;
            }
        }
        int kept = zeroDice ? lowest : highest;
        int keptSixes = zeroDice ? (kept == 6 ? 1 : 0) : sixes;
        return new DiceRoll(results, kept, lowest, keptSixes, zeroDice);
    }

    /**
     * Core Rulebook p. 150: match the crew's current heat to a column, then use
     * the wanted-level dice result to pick the row. Rows with "A or B" leave the
     * choice to the GM/fiction.
     */
    private EntanglementResult entanglementFor(int heat, int roll) {
        String column;
        String[] row;
        if (heat <= 3) {
            column = "HEAT 0-3";
            row = switch (roll) {
                case 1, 2, 3 -> new String[] {"Gang Trouble", "The Usual Suspects"};
                case 4, 5 -> new String[] {"Rivals", "Unquiet Dead"};
                default -> new String[] {"Cooperation"};
            };
        } else if (heat <= 5) {
            column = "HEAT 4/5";
            row = switch (roll) {
                case 1, 2, 3 -> new String[] {"Gang Trouble", "Questioning"};
                case 4, 5 -> new String[] {"Reprisals", "Unquiet Dead"};
                default -> new String[] {"Show of Force"};
            };
        } else {
            column = "HEAT 6+";
            row = switch (roll) {
                case 1, 2, 3 -> new String[] {"Flipped", "Interrogation"};
                case 4, 5 -> new String[] {"Demonic Notice", "Show of Force"};
                default -> new String[] {"Arrest"};
            };
        }

        List<String> options = List.of(row);
        String name = options.size() == 1 ? options.get(0) : options.get(0) + " or " + options.get(1);
        StringBuilder description = new StringBuilder();
        description.append(column).append(", roll ").append(roll).append(". ");
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) {
                description.append(" OR ");
            }
            String option = options.get(i);
            description.append(option).append(": ").append(entanglementText(option));
        }
        if (options.size() > 1) {
            description.append(" (GM chooses which manifests.)");
        }
        return new EntanglementResult(name, description.toString(), options, roll, column);
    }

    /** Resolution text from Core Rulebook pp. 151–152. */
    private static String entanglementText(String name) {
        return switch (name) {
            case "Arrest" ->
                "An Inspector presents a case file to a magistrate. Bluecoats send a detail "
                    + "(scale at least equal to your wanted level). Pay coin equal to wanted level +3, "
                    + "hand someone over for arrest (clears heat), or try to evade capture.";
            case "Cooperation" ->
                "A +3 status faction asks for a favor. Agree, forfeit 1 rep per Tier of that faction, "
                    + "or lose 1 status with them. If you have no +3 faction, you avoid entanglements now.";
            case "Demonic Notice" ->
                "A demon approaches with a dark offer. Accept their bargain, hide until it loses interest "
                    + "(forfeit 3 rep), or deal with it another way.";
            case "Flipped" ->
                "One of a PC's rivals turns a contact, patron, client, or customers against you due to the heat. "
                    + "They're loyal to another faction now.";
            case "Gang Trouble" ->
                "One of your gangs (or other cohorts) causes trouble due to their flaw(s). Lose face "
                    + "(forfeit rep equal to Tier +1), make an example of a member, or face reprisals.";
            case "Interrogation" ->
                "The Bluecoats round up one of the PCs. Pay them off with 3 coin, or they beat you "
                    + "(level 2 harm) and you tell them what they want (+3 heat). Resist each separately.";
            case "Questioning" ->
                "The Bluecoats grab an NPC crew member or contact. Fortune roll for how much they talk "
                    + "(1-3: +2 heat, 4/5: +1 heat), or pay 2 coin.";
            case "Reprisals" ->
                "An enemy faction moves against you (or a friend, contact, or vice purveyor). Pay 1 rep and "
                    + "1 coin per Tier of the enemy, allow them to mess with you, or fight back.";
            case "Rivals" ->
                "A neutral faction throws their weight around against you, a friend, contact, or vice purveyor. "
                    + "Forfeit 1 rep or 1 coin per Tier of the rival, or stand up to them and lose 1 status.";
            case "Show of Force" ->
                "A faction with negative status plays against your holdings. Give them 1 claim or go to war "
                    + "(-3 status). If you have no claims, lose 1 hold instead.";
            case "Unquiet Dead" ->
                "A rogue spirit is drawn to you. Acquire a Whisper or Rail Jack to banish it, or deal with it yourself.";
            case "The Usual Suspects" ->
                "The Bluecoats grab someone on your periphery (volunteer a friend or vice purveyor). Fortune roll "
                    + "if they resist (1-3: +2 heat, 4/5: level 2 harm), or pay 1 coin.";
            default -> "See Core Rulebook p. 151–152.";
        };
    }
}
