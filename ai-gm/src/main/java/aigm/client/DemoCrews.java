package aigm.client;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.Heat;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Background;
import aigm.gamestate.player.Harm;
import aigm.gamestate.player.Heritage;
import aigm.gamestate.player.Loadout;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.player.Vice;
import aigm.gamestate.player.ViceKind;

/** Shared demo crew for CLI / starter smoke tests. */
public final class DemoCrews {

    private DemoCrews() {}

    public static Crew nightspires() {
        Player scoundrel = new Player(
            "Ilyas",
            Heritage.AKOROS,
            Background.UNDERWORLD,
            new Vice(ViceKind.GAMBLING, "The Six Towers dens"),
            new Trauma(),
            Map.of(
                Action.PROWL, 2,
                Action.FINESSE, 1,
                Action.SKIRMISH, 1
            ),
            new Harm(),
            1,
            0,
            PlaybookEnum.LURK,
            List.<Ability>of(),
            new Advancement(),
            new Loadout(),
            null,
            null,
            "Whisper",
            "lean Akorosi in a dark coat",
            "Doskvol native",
            "Crow's Foot gangs"
        );

        return new Crew(
            "The Nightspires",
            CrewTypeEnum.SHADOWS,
            "Crow's Foot loft",
            "Crow's Foot",
            List.of(scoundrel),
            2,
            new Heat(),
            new CrewStanding(),
            new Clock("Crew XP", Crew.CREW_XP_BOXES),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            Map.of()
        );
    }
}
