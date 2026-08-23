package aigm.gamestate.campaign;

/** Entanglements from Core Rulebook pp. 150–152. */
public enum Entanglement {

    ARREST(
        "Arrest",
        "An Inspector presents a case file to a magistrate. Bluecoats send a detail "
            + "(scale at least equal to your wanted level). Pay coin equal to wanted level +3, "
            + "hand someone over for arrest (clears heat), or try to evade capture."),
    COOPERATION(
        "Cooperation",
        "A +3 status faction asks for a favor. Agree, forfeit 1 rep per Tier of that faction, "
            + "or lose 1 status with them. If you have no +3 faction, you avoid entanglements now."),
    DEMONIC_NOTICE(
        "Demonic Notice",
        "A demon approaches with a dark offer. Accept their bargain, hide until it loses interest "
            + "(forfeit 3 rep), or deal with it another way."),
    FLIPPED(
        "Flipped",
        "One of a PC's rivals turns a contact, patron, client, or customers against you due to the heat. "
            + "They're loyal to another faction now."),
    GANG_TROUBLE(
        "Gang Trouble",
        "One of your gangs (or other cohorts) causes trouble due to their flaw(s). Lose face "
            + "(forfeit rep equal to Tier +1), make an example of a member, or face reprisals."),
    INTERROGATION(
        "Interrogation",
        "The Bluecoats round up one of the PCs. Pay them off with 3 coin, or they beat you "
            + "(level 2 harm) and you tell them what they want (+3 heat). Resist each separately."),
    QUESTIONING(
        "Questioning",
        "The Bluecoats grab an NPC crew member or contact. Fortune roll for how much they talk "
            + "(1-3: +2 heat, 4/5: +1 heat), or pay 2 coin."),
    REPRISALS(
        "Reprisals",
        "An enemy faction moves against you (or a friend, contact, or vice purveyor). Pay 1 rep and "
            + "1 coin per Tier of the enemy, allow them to mess with you, or fight back."),
    RIVALS(
        "Rivals",
        "A neutral faction throws their weight around against you, a friend, contact, or vice purveyor. "
            + "Forfeit 1 rep or 1 coin per Tier of the rival, or stand up to them and lose 1 status."),
    SHOW_OF_FORCE(
        "Show of Force",
        "A faction with negative status plays against your holdings. Give them 1 claim or go to war "
            + "(-3 status). If you have no claims, lose 1 hold instead."),
    UNQUIET_DEAD(
        "Unquiet Dead",
        "A rogue spirit is drawn to you. Acquire a Whisper or Rail Jack to banish it, or deal with it yourself."),
    THE_USUAL_SUSPECTS(
        "The Usual Suspects",
        "The Bluecoats grab someone on your periphery (volunteer a friend or vice purveyor). Fortune roll "
            + "if they resist (1-3: +2 heat, 4/5: level 2 harm), or pay 1 coin.");

    private final String name;
    private final String resolution;

    Entanglement(String name, String resolution) {
        this.name = name;
        this.resolution = resolution;
    }

    public String getName() {
        return name;
    }

    public String getResolution() {
        return resolution;
    }
}
