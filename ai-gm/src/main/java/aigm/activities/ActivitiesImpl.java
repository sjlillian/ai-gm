package aigm.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import aigm.gamestate.DiceRoll;
import aigm.gamestate.campaign.Entanglement;
import aigm.gamestate.campaign.Heat;

/**
 * Dice pools and Core Rulebook table lookups.
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
        List<Entanglement> options;
        if (heat <= 3) {
            column = "HEAT 0-3";
            options = switch (roll) {
                case 1, 2, 3 -> List.of(Entanglement.GANG_TROUBLE, Entanglement.THE_USUAL_SUSPECTS);
                case 4, 5 -> List.of(Entanglement.RIVALS, Entanglement.UNQUIET_DEAD);
                default -> List.of(Entanglement.COOPERATION);
            };
        } else if (heat <= 5) {
            column = "HEAT 4/5";
            options = switch (roll) {
                case 1, 2, 3 -> List.of(Entanglement.GANG_TROUBLE, Entanglement.QUESTIONING);
                case 4, 5 -> List.of(Entanglement.REPRISALS, Entanglement.UNQUIET_DEAD);
                default -> List.of(Entanglement.SHOW_OF_FORCE);
            };
        } else {
            column = "HEAT 6+";
            options = switch (roll) {
                case 1, 2, 3 -> List.of(Entanglement.FLIPPED, Entanglement.INTERROGATION);
                case 4, 5 -> List.of(Entanglement.DEMONIC_NOTICE, Entanglement.SHOW_OF_FORCE);
                default -> List.of(Entanglement.ARREST);
            };
        }

        String name = options.size() == 1
            ? options.get(0).getName()
            : options.get(0).getName() + " or " + options.get(1).getName();
        StringBuilder description = new StringBuilder();
        description.append(column).append(", roll ").append(roll).append(". ");
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) {
                description.append(" OR ");
            }
            Entanglement option = options.get(i);
            description.append(option.getName()).append(": ").append(option.getResolution());
        }
        if (options.size() > 1) {
            description.append(" (GM chooses which manifests.)");
        }
        return new EntanglementResult(name, description.toString(), options, roll, column);
    }
}
