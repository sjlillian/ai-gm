package aigm.gamestate.player;

import aigm.gamestate.Clock;

/**
 * Playbook XP track is 8 boxes; each attribute XP track is 6.
 * Desperate action rolls mark the matching attribute XP.
 */
public record Advancement(
    Clock playbookXp,
    Clock insightXp,
    Clock prowessXp,
    Clock resolveXp
) {

    public Advancement() {
        this(
            new Clock("Playbook XP", 8),
            new Clock("Insight XP", 6),
            new Clock("Prowess XP", 6),
            new Clock("Resolve XP", 6)
        );
    }

    public Clock trackFor(Attribute attribute) {
        return switch (attribute) {
            case INSIGHT -> insightXp;
            case PROWESS -> prowessXp;
            case RESOLVE -> resolveXp;
        };
    }

    public Advancement markPlaybookXp(int amount) {
        return new Advancement(playbookXp.tick(amount), insightXp, prowessXp, resolveXp);
    }

    public Advancement markAttributeXp(Attribute attribute, int amount) {
        return switch (attribute) {
            case INSIGHT -> new Advancement(playbookXp, insightXp.tick(amount), prowessXp, resolveXp);
            case PROWESS -> new Advancement(playbookXp, insightXp, prowessXp.tick(amount), resolveXp);
            case RESOLVE -> new Advancement(playbookXp, insightXp, prowessXp, resolveXp.tick(amount));
        };
    }

    public Advancement resetPlaybookXp() {
        return new Advancement(playbookXp.withProgress(0), insightXp, prowessXp, resolveXp);
    }

    public Advancement resetAttributeXp(Attribute attribute) {
        return switch (attribute) {
            case INSIGHT -> new Advancement(playbookXp, insightXp.withProgress(0), prowessXp, resolveXp);
            case PROWESS -> new Advancement(playbookXp, insightXp, prowessXp.withProgress(0), resolveXp);
            case RESOLVE -> new Advancement(playbookXp, insightXp, prowessXp, resolveXp.withProgress(0));
        };
    }

    public Advancement mark(XpTrack track, int amount) {
        return switch (track) {
            case PLAYBOOK -> markPlaybookXp(amount);
            case INSIGHT -> markAttributeXp(Attribute.INSIGHT, amount);
            case PROWESS -> markAttributeXp(Attribute.PROWESS, amount);
            case RESOLVE -> markAttributeXp(Attribute.RESOLVE, amount);
        };
    }

    public enum XpTrack {
        PLAYBOOK, INSIGHT, PROWESS, RESOLVE
    }
}
