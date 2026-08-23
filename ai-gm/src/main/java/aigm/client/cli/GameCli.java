package aigm.client.cli;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import aigm.client.CampaignSnapshot;
import aigm.client.DemoCrews;
import aigm.client.TemporalGameClient;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.score.ScoreType;
import aigm.llm.LlmActivities;
import aigm.workflow.ActionRollResult;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.DowntimeActivityChoice;
import aigm.workflow.ScoreEndRequest;
import aigm.workflow.ScoreRequest;

/**
 * Plain stdin/stdout adapter over {@link TemporalGameClient}.
 */
public final class GameCli {

    private final TemporalGameClient game;
    private final Scanner in;
    private final PrintStream out;

    public GameCli(TemporalGameClient game, Scanner in, PrintStream out) {
        this.game = game;
        this.in = in;
        this.out = out;
    }

    public static void main(String[] args) {
        try (TemporalGameClient client = new TemporalGameClient()) {
            new GameCli(client, new Scanner(System.in), System.out).run(args);
        }
    }

    public void run(String[] args) {
        out.println("AI-GM CLI. Type help. Worker must already be running.");
        if (args.length > 0 && "new".equalsIgnoreCase(args[0])) {
            startNew(args.length > 1 ? args[1] : "campaign-demo");
        } else if (args.length > 1 && "attach".equalsIgnoreCase(args[0])) {
            game.attach(args[1]);
            out.println("attached " + args[1]);
            printStatus();
        }

        while (true) {
            out.print("> ");
            out.flush();
            if (!in.hasNextLine()) {
                break;
            }
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                if (!handle(line)) {
                    break;
                }
            } catch (RuntimeException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private boolean handle(String line) {
        String[] p = line.split("\\s+");
        String cmd = p[0].toLowerCase(Locale.ROOT);
        switch (cmd) {
            case "help", "?" -> printHelp();
            case "new" -> startNew(p.length > 1 ? p[1] : "campaign-demo");
            case "attach" -> {
                require(p, 2, "attach <campaignId>");
                game.attach(p[1]);
                out.println("attached " + p[1]);
                printStatus();
            }
            case "status", "s" -> printStatus();
            case "score" -> {
                require(p, 6, "score <title> <PLAN> <target> <tier> <dice>");
                ScoreType plan = ScoreType.valueOf(p[2].toUpperCase(Locale.ROOT));
                game.startScore(new ScoreRequest(
                    null,
                    p[1],
                    plan,
                    plan.name(),
                    p[3],
                    parseTier(p[4]),
                    Integer.parseInt(p[5]),
                    null,
                    List.of()
                ));
                out.println("score started");
                printStatus();
            }
            case "adjudicate", "adj" -> {
                require(p, 3, "adjudicate <ACTION> <situation...>");
                Action action = Action.valueOf(p[1].toUpperCase(Locale.ROOT));
                String situation = join(p, 2);
                LlmActivities.Adjudication adj = game.adjudicate(situation, situation, action);
                out.println("action=" + adj.action()
                    + " position=" + adj.position()
                    + " effect=" + adj.effect());
                out.println("reasoning: " + adj.reasoning());
                out.println("stakes: " + adj.possibleStakes());
            }
            case "roll" -> {
                require(p, 6, "roll <pc> <ACTION> <rating> <POSITION> <EFFECT> [push] [assist]");
                ActionRollResult roll = game.resolveAction(
                    p[1],
                    Action.valueOf(p[2].toUpperCase(Locale.ROOT)),
                    Integer.parseInt(p[3]),
                    Position.valueOf(p[4].toUpperCase(Locale.ROOT)),
                    Effect.valueOf(p[5].toUpperCase(Locale.ROOT)),
                    hasFlag(p, "push"),
                    hasFlag(p, "assist"),
                    ""
                );
                out.println(roll);
            }
            case "clock" -> {
                require(p, 3, "clock <id> <segments>");
                game.tickClock(p[1], Integer.parseInt(p[2]));
                out.println("ok");
            }
            case "endscore" -> {
                require(p, 2, "endscore <success|fail> [baseHeat]");
                boolean success = p[1].equalsIgnoreCase("success") || p[1].equalsIgnoreCase("win");
                int heat = p.length > 2 ? Integer.parseInt(p[2]) : 2;
                CampaignSnapshot snap = game.snapshot();
                int tier = snap.crew().crewStanding().tier().ordinal();
                game.endScore(ScoreEndRequest.simple(success, tier, heat));
                out.println("score ended");
                printStatus();
            }
            case "downtime", "dt" -> {
                require(p, 3, "downtime <pc> <KIND> [details...]");
                DowntimeActivityChoice.Kind kind =
                    DowntimeActivityChoice.Kind.valueOf(p[2].toUpperCase(Locale.ROOT));
                String details = p.length > 3 ? join(p, 3) : "";
                boolean paid = details.toLowerCase(Locale.ROOT).endsWith(" pay");
                if (paid) {
                    details = details.substring(0, details.length() - 4).trim();
                }
                game.chooseDowntimeActivity(
                    p[1],
                    new DowntimeActivityChoice(kind, p[1], details, paid)
                );
                out.println("activity queued");
            }
            case "closedowntime", "cd" -> {
                game.closeDowntime();
                out.println("downtime closed");
                printStatus();
            }
            case "sheet" -> {
                require(p, 2, "sheet <pc>");
                Player player = game.getPlayer(p[1]);
                out.println(player);
            }
            case "trauma" -> {
                require(p, 3, "trauma <pc> <CONDITION>");
                game.markTrauma(p[1], Trauma.Condition.valueOf(p[2].toUpperCase(Locale.ROOT)));
                out.println("ok");
            }
            case "end" -> {
                game.endCampaign();
                out.println("campaign end signaled");
            }
            case "quit", "exit", "q" -> {
                return false;
            }
            default -> out.println("unknown: " + cmd + " (help)");
        }
        return true;
    }

    private void startNew(String id) {
        String started = game.startCampaign(DemoCrews.nightspires(), id);
        out.println("started " + started);
        printStatus();
    }

    private void printStatus() {
        CampaignSnapshot snap = game.snapshot();
        out.println("--- " + snap.campaignWorkflowId() + " ---");
        out.println("phase=" + snap.phase() + " cycle=" + snap.cycleNumber());
        out.println("crew=" + snap.crew().name()
            + " coin=" + snap.crew().coin()
            + " heat=" + snap.crew().heat().heat().progress()
            + "/" + snap.crew().heat().heat().max()
            + " wanted=" + snap.crew().heat().wantedLevel()
            + " tier=" + snap.crew().crewStanding().tier());
        out.println("pcs=" + snap.pcWorkflowIds());
        if (snap.phase() == CampaignWorkflow.Phase.SCORE) {
            out.println("score=" + snap.activeScoreWorkflowId());
            out.println("engagement=" + snap.engagementPosition());
            out.println("clocks=" + snap.scoreClocks());
            out.println("adjudication=" + snap.lastAdjudication());
        }
        if (snap.phase() == CampaignWorkflow.Phase.DOWNTIME) {
            out.println("downtime=" + snap.activeDowntimeWorkflowId());
            out.println("choices=" + snap.downtimeChoices());
        }
    }

    private void printHelp() {
        out.println("""
            new [id]
            attach <id>
            status
            score <title> <PLAN> <target> <tier> <dice>
              PLAN=ASSAULT|DECEPTION|STEALTH|OCCULT|SOCIAL|TRANSPORT
            adjudicate <ACTION> <situation...>
            roll <pc> <ACTION> <rating> <POSITION> <EFFECT> [push] [assist]
            clock <id> <segments>
            endscore <success|fail> [baseHeat]
            downtime <pc> <KIND> [details] [pay]
              KIND=ACQUIRE_ASSET|LONG_TERM_PROJECT|RECOVER|REDUCE_HEAT|TRAIN|INDULGE_VICE
            closedowntime
            sheet <pc>
            trauma <pc> <CONDITION>
            end
            quit
            """);
    }

    private static void require(String[] p, int min, String usage) {
        if (p.length < min) {
            throw new IllegalArgumentException(usage);
        }
    }

    private static String join(String[] p, int from) {
        return String.join(" ", Arrays.copyOfRange(p, from, p.length));
    }

    private static boolean hasFlag(String[] p, String flag) {
        for (String part : p) {
            if (part.equalsIgnoreCase(flag)) {
                return true;
            }
        }
        return false;
    }

    private static CrewStanding.Tier parseTier(String raw) {
        try {
            int n = Integer.parseInt(raw);
            CrewStanding.Tier[] values = CrewStanding.Tier.values();
            return values[Math.max(0, Math.min(n, values.length - 1))];
        } catch (NumberFormatException e) {
            return CrewStanding.Tier.valueOf(raw.toUpperCase(Locale.ROOT));
        }
    }
}
