package ch.zli.mm233.console;

import ch.zli.mm233.engine.GameEngine;
import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.PendingAction;
import ch.zli.mm233.engine.model.Phase;
import ch.zli.mm233.engine.model.Player;
import ch.zli.mm233.engine.model.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ConsoleApp {

    private final ConsoleUi ui;

    public ConsoleApp(ConsoleUi ui) {
        this.ui = ui;
    }

    public static void main(String[] args) {
        ConsoleUi ui = new ConsoleUi(new Scanner(System.in), System.out);
        new ConsoleApp(ui).run();
    }

    public void run() {
        ui.println("=== Secret Hitler (basic console) ===");
        List<String> names = readNames();
        GameState state = GameEngine.newGame(names);
        printSecretRoles(state);
        while (state.phase() != Phase.GAME_OVER) {
            state = runRound(state);
        }
        ui.blank();
        ui.println("GAME OVER - " + state.winner());
    }

    private void printSecretRoles(GameState state) {
        ui.blank();
        ui.println("=== SECRET ROLE ASSIGNMENTS ===");
        state.players().forEach(p ->
                ui.println("  " + p.name() + " -> " + p.role()));
        ui.blank();
        ui.println("Press ENTER to continue (clear screen first!)");
        ui.promptLine("");
    }

    private List<String> readNames() {
        int n = ui.promptInt("How many players?", 5, 10);
        List<String> names = new ArrayList<>(n);
        IntStream.range(0, n).forEach(i ->
                names.add(ui.promptLine("Name of player " + i)));
        return List.copyOf(names);
    }

    private GameState runRound(GameState s) {
        printPublicState(s);
        GameState afterNominate = nominateChancellor(s);
        int chancellorIdx = ((PendingAction.Election) afterNominate.pendingAction())
                .chancellorCandidateIndex();
        GameState afterVotes = collectVotes(afterNominate);
        GameState resolved = GameEngine.resolveElection(afterVotes);

        if (resolved.phase() == Phase.GAME_OVER) {
            return resolved;
        }
        if (resolved.phase() == Phase.ELECTION) {
            ui.println("Vote FAILED. Election tracker: " + resolved.electionTracker() + "/3.");
            return resolved;
        }
        ui.println("Vote PASSED. Chancellor is "
                + s.players().get(chancellorIdx).name() + ".");
        return runLegislativeSession(resolved, chancellorIdx);
    }

    private void printPublicState(GameState s) {
        ui.blank();
        ui.println("--- Round ---");
        ui.println("Liberal: " + s.liberalPolicies() + "/5    Fascist: "
                + s.fascistPolicies() + "/6    Tracker: " + s.electionTracker() + "/3");
        ui.println("President: " + s.players().get(s.presidentIndex()).name()
                + " (#" + s.presidentIndex() + ")");
    }

    private GameState nominateChancellor(GameState s) {
        List<Player> eligible = s.players().stream()
                .filter(p -> GameEngine.isEligibleChancellor(s, p.id()))
                .toList();
        ui.println("Eligible chancellors:");
        eligible.forEach(p -> ui.println("  " + p.id() + " = " + p.name()));
        while (true) {
            int idx = ui.promptInt("President picks chancellor",
                    0, s.players().size() - 1);
            try {
                return GameEngine.nominateChancellor(s, idx);
            } catch (IllegalArgumentException e) {
                ui.println("  " + e.getMessage() + " — try again");
            }
        }
    }

    private GameState collectVotes(GameState s) {
        List<Player> voters = s.players().stream().filter(Player::alive).toList();
        GameState current = s;
        for (Player p : voters) {
            current = GameEngine.castVote(current, p.id(),
                    ui.promptYesNo(p.name() + " votes"));
        }
        return current;
    }

    private GameState runLegislativeSession(GameState s, int chancellorIdx) {
        GameEngine.DrawResult dr = GameEngine.drawThree(s);
        List<Policy> drawn = dr.drawn();
        String prezName = s.players().get(s.presidentIndex()).name();
        String chancName = s.players().get(chancellorIdx).name();

        ui.println(prezName + " (President) drew: " + drawn);
        int presDiscardIdx = ui.promptInt(prezName + ", which card to DISCARD?", 0, 2);
        Policy presDiscard = drawn.get(presDiscardIdx);
        List<Policy> twoCards = removeIndex(drawn, presDiscardIdx);

        ui.println(chancName + " (Chancellor) receives: " + twoCards);
        int chancDiscardIdx = ui.promptInt(chancName + ", which card to DISCARD?", 0, 1);
        Policy chancDiscard = twoCards.get(chancDiscardIdx);
        Policy enacted = twoCards.get(1 - chancDiscardIdx);

        ui.println("Policy enacted: " + enacted);
        return GameEngine.enactPolicy(dr.state(), enacted, presDiscard, chancDiscard);
    }

    private static <T> List<T> removeIndex(List<T> list, int idx) {
        return IntStream.range(0, list.size())
                .filter(i -> i != idx)
                .mapToObj(list::get)
                .toList();
    }
}
