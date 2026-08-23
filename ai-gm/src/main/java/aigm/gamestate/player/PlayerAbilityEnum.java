package aigm.gamestate.player;

import aigm.gamestate.Ability;

/** Playbook special abilities from Core Rulebook pp. 62–86. */
public enum PlayerAbilityEnum implements Ability {

    // --- Cutter (p. 62) ---
    BATTLEBORN(
        "Battleborn",
        "You may spend your special armor to reduce harm from an attack in combat, or to push yourself during a fight."),
    BODYGUARD(
        "Bodyguard",
        "When you protect a teammate, take +1d to your resistance roll. When you gather information to anticipate possible threats in the current situation, you get +1 effect."),
    GHOST_FIGHTER(
        "Ghost Fighter",
        "You may imbue your hands, melee weapons, or tools with spirit energy. You gain potency in combat vs. the supernatural. You may grapple with spirits to restrain and capture them."),
    LEADER(
        "Leader",
        "When you Command a cohort in combat, they continue to fight when they would otherwise break (they're not taken out when they suffer level 3 harm). They gain +1 effect and 1 armor."),
    MULE(
        "Mule",
        "Your load limits are higher. Light: 5. Normal: 7. Heavy: 8."),
    NOT_TO_BE_TRIFLED_WITH(
        "Not to Be Trifled With",
        "You can push yourself to perform a feat of physical force that verges on the superhuman, or engage a small gang on equal footing in close combat."),
    SAVAGE(
        "Savage",
        "When you unleash physical violence, it's especially frightening. When you Command a frightened target, take +1d."),
    VIGOROUS(
        "Vigorous",
        "You recover from harm faster. Permanently fill in one of your healing clock segments. Take +1d to healing treatment rolls."),

    // --- Hound (p. 66) ---
    SHARPSHOOTER(
        "Sharpshooter",
        "You can push yourself to make a ranged attack at extreme distance beyond what's normal for the weapon, or unleash a barrage of rapid fire to suppress the enemy."),
    FOCUSED(
        "Focused",
        "You may spend your special armor to resist a consequence of surprise or mental harm, or to push yourself for ranged combat or tracking."),
    GHOST_HUNTER(
        "Ghost Hunter",
        "Your hunting pet is imbued with spirit energy. It gains potency when tracking or fighting the supernatural, and gains an arcane ability: ghost-form, mind-link, or arrow-swift. Take this ability again to choose an additional arcane ability for your pet."),
    SCOUT(
        "Scout",
        "When you gather information to discover the location of a target, you get +1 effect. When you hide in a prepared position or use camouflage, you get +1d to rolls to avoid detection."),
    SURVIVOR(
        "Survivor",
        "From hard-won experience or occult ritual, you are immune to the poisonous miasma of the deathlands and can subsist on the strange flora and fauna there. You get +1 stress box."),
    TOUGH_AS_NAILS(
        "Tough as Nails",
        "Penalties from harm are one level less severe (though level 4 harm is still fatal)."),
    VENGEFUL(
        "Vengeful",
        "You gain an additional xp trigger: You got payback against someone who harmed you or someone you care about. If your crew helped you get payback, also mark crew xp."),

    // --- Leech (p. 70) ---
    ALCHEMIST(
        "Alchemist",
        "When you invent or craft a creation with alchemical features, take +1d to your roll. You begin with one special formula already known."),
    ARTIFICER(
        "Artificer",
        "When you invent or craft a creation with spark-craft features, take +1d to your roll. You begin with one special design already known."),
    ANALYST(
        "Analyst",
        "During downtime, you get two ticks to distribute among any long-term project clocks that involve investigation or learning a new formula or design plan."),
    FORTITUDE(
        "Fortitude",
        "You may spend your special armor to resist a consequence of fatigue, weakness, or chemical effects, or to push yourself when working with technical skill."),
    GHOST_WARD(
        "Ghost Ward",
        "When you Wreck an area with arcane substances, ruining it for any other use, it becomes anathema or enticing to spirits (your choice)."),
    PHYSICKER(
        "Physicker",
        "You can Tinker with bones, blood, and bodily humours to treat wounds or stabilize the dying. You may Study a malady or corpse. Everyone in your crew (including you) gets +1d to their healing treatment rolls."),
    SABOTEUR(
        "Saboteur",
        "When you Wreck, your work is much quieter than it should be and the damage is very well-hidden from casual inspection."),
    VENOMOUS(
        "Venomous",
        "Choose a drug or poison (from your bandolier stock) to which you have become immune. You can push yourself to secrete it through your skin or saliva or exhale it as a vapor."),

    // --- Lurk (p. 73) ---
    INFILTRATOR(
        "Infiltrator",
        "You are not affected by quality or Tier when you bypass security measures."),
    AMBUSH(
        "Ambush",
        "When you attack from hiding or spring a trap, you get +1d to your roll."),
    DAREDEVIL(
        "Daredevil",
        "When you roll a desperate action, you get +1d to your roll if you also take −1d to any resistance rolls against consequences from your action."),
    THE_DEVILS_FOOTSTEPS(
        "The Devil's Footsteps",
        "You can push yourself to perform a feat of athletics that verges on the superhuman, or maneuver to confuse your enemies so they mistakenly attack each other."),
    EXPERTISE(
        "Expertise",
        "Choose one of your action ratings. When you lead a group action using that action, you can suffer only 1 stress at most, regardless of the number of failed rolls."),
    GHOST_VEIL(
        "Ghost Veil",
        "You may shift partially into the ghost field, becoming shadowy and insubstantial for a few moments. Take 2 stress when you shift, plus 1 stress for each extra feature: it lasts for a few minutes rather than moments — you are invisible rather than shadowy — you may float through the air like a ghost."),
    REFLEXES(
        "Reflexes",
        "When there's a question about who acts first, the answer is you."),
    SHADOW(
        "Shadow",
        "You may spend your special armor to resist a consequence from detection or security measures, or to push yourself for a feat of athletics or stealth."),

    // --- Slide (p. 77) ---
    ROOKS_GAMBIT(
        "Rook's Gambit",
        "Take 2 stress to roll your best action rating while performing a different action. Say how you adapt your skill to this use."),
    CLOAK_AND_DAGGER(
        "Cloak & Dagger",
        "When you use a disguise or other form of covert misdirection, you get +1d to rolls to confuse or deflect suspicion. When you throw off your disguise, the resulting surprise gives you the initiative in the situation."),
    GHOST_VOICE(
        "Ghost Voice",
        "You know the secret method to interact with a ghost or demon as if it were a normal human, regardless of how wild or feral it appears. You gain potency when communicating with the supernatural."),
    A_LITTLE_SOMETHING_ON_THE_SIDE(
        "A Little Something on the Side",
        "At the end of each downtime phase, you earn +2 stash."),
    LIKE_LOOKING_INTO_A_MIRROR(
        "Like Looking into a Mirror",
        "You can always tell when someone is lying to you."),
    MESMERISM(
        "Mesmerism",
        "When you Sway someone, you may cause them to forget that it's happened until they next interact with you."),
    SUBTERFUGE(
        "Subterfuge",
        "You may spend your special armor to resist a consequence from suspicion or persuasion, or to push yourself for subterfuge."),
    TRUST_IN_ME(
        "Trust in Me",
        "You get +1d vs. a target with whom you have an intimate relationship."),

    // --- Spider (p. 81) ---
    FORESIGHT(
        "Foresight",
        "Two times per score you can assist a teammate without paying stress. Describe how you prepared for this."),
    CALCULATING(
        "Calculating",
        "Due to your careful planning, during downtime, you may give yourself or another crew member +1 downtime activity."),
    CONNECTED(
        "Connected",
        "During downtime, you get +1 result level when you acquire an asset or reduce heat."),
    FUNCTIONING_VICE(
        "Functioning Vice",
        "When you indulge your vice, you may adjust the dice outcome by 1 or 2 (up or down). An ally who joins you may do the same."),
    GHOST_CONTRACT(
        "Ghost Contract",
        "When you shake on a deal or draft one in writing, you and your partner—human or otherwise—both bear a mark of your oath. If either breaks the contract, they take level 3 harm, \"Cursed\"."),
    JAIL_BIRD(
        "Jail Bird",
        "When incarcerated, your wanted level counts as 1 less, your Tier as 1 more, and you gain +1 faction status with a faction that you help while you're inside, in addition to whatever you get from the incarceration roll."),
    MASTERMIND(
        "Mastermind",
        "You may spend your special armor to protect a teammate, or to push yourself when you gather information or work on a long-term project."),
    WEAVING_THE_WEB(
        "Weaving the Web",
        "You gain +1d to Consort when you gather information on a target for a score. You get +1d to the engagement roll for that operation."),

    // --- Whisper (p. 86) ---
    COMPEL(
        "Compel",
        "You can Attune to the ghost field to force a nearby spirit to appear before you and obey a command you give it. You are not supernaturally terrified by a spirit you summon or attempt to compel."),
    GHOST_MIND(
        "Ghost Mind",
        "You're always aware of supernatural entities in your presence. Take +1d whenever you gather information about the supernatural by any means."),
    IRON_WILL(
        "Iron Will",
        "You are immune to the terror that some supernatural entities inflict on sight. When you make a resistance roll with Resolve, take +1d."),
    OCCULTIST(
        "Occultist",
        "You know the secret ways to Consort with ancient powers, forgotten gods, or demons. Once you've consorted with one, you get +1d to Command cultists who worship it."),
    RITUAL(
        "Ritual",
        "You know the arcane methods to perform ritual sorcery. You can Study an occult ritual (or create a new one) to summon a supernatural effect or being. You begin with one ritual already learned."),
    STRANGE_METHODS(
        "Strange Methods",
        "When you invent or craft a creation with arcane features, take +1d to your roll. You begin with one arcane design already known."),
    TEMPEST(
        "Tempest",
        "You can push yourself to unleash a stroke of lightning as a weapon, or summon a storm in your immediate vicinity."),
    WARDED(
        "Warded",
        "You may spend your special armor to resist a supernatural consequence, or to push yourself when you contend with or employ arcane forces.");

    private final String name;
    private final String description;

    PlayerAbilityEnum(String name, String description) {
        this.name = name;
        this.description = description;
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
    public Scope getScope() {
        return Scope.PLAYER;
    }

}
