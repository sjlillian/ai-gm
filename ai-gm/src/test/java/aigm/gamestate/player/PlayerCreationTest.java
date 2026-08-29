package aigm.gamestate.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerCreationTest {

    @Test
    void draftIsIncomplete() {
        Player draft = Player.draft("p1");
        assertEquals("p1", draft.name());
        assertFalse(draft.isCreationComplete());
        assertEquals(0, draft.extraActionDots());
    }

    @Test
    void extraDotsIgnorePrintedPlaybookRatings() {
        Player lurk = Player.draft("p1")
            .withPlaybook(PlaybookEnum.LURK)
            .withActionRatings(PlaybookEnum.LURK.getStartingActionRatings());
        assertEquals(0, lurk.extraActionDots());

        Player oneDot = lurk.withActionRating(Action.SKIRMISH, 1);
        assertEquals(1, oneDot.extraActionDots());
        assertEquals(2, oneDot.getActionRating(Action.PROWL));
    }

    @Test
    void creationRejectsBlankJoinId() {
        assertThrows(IllegalArgumentException.class, () -> Player.draft(""));
    }

    @Test
    void completeSheetNeedsIdentityAndFourDots() {
        Player player = Player.draft("p1")
            .withPlaybook(PlaybookEnum.LURK)
            .withActionRatings(PlaybookEnum.LURK.getStartingActionRatings())
            .withHeritage(Heritage.AKOROS, "Crow's Foot")
            .withBackground(Background.UNDERWORLD, "street kid")
            .withActionRating(Action.SKIRMISH, 1)
            .withActionRating(Action.HUNT, 1)
            .withActionRating(Action.SURVEY, 1)
            .withActionRating(Action.FINESSE, 2)
            .withAbilities(java.util.List.of(PlayerAbilityEnum.INFILTRATOR))
            .withFriend(PlayerContactEnum.LURK_TELDA)
            .withRival(PlayerContactEnum.LURK_DARMOT)
            .withVice(new Vice(ViceKind.GAMBLING, "The Six Towers dens"))
            .withIdentity("Ilyas", "Whisper", "lean in a dark coat");
        assertEquals(4, player.extraActionDots());
        assertTrue(player.isCreationComplete());
    }
}
