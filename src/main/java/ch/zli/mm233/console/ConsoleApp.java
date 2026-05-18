package ch.zli.mm233.console;

import ch.zli.mm233.engine.GameEngine;
import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.Phase;
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
        int chancellorIdx = nominateChancellor(s);
        boolean elected = collectVotes(s);
        if (!elected) {
            ui.println("Vote FAILED. Next president.");
            return GameEngine.failedElection(s);
        }
        ui.println("Vote PASSED. Chancellor is " + s.players().get(chancellorIdx).name() + ".");
        return runLegislativeSession(s, chancellorIdx);
    }

    private void printPublicState(GameState s) {
        ui.blank();
        ui.println("--- Round ---");
        ui.println("Liberal: " + s.liberalPolicies() + "/5    Fascist: " + s.fascistPolicies() + "/6");
        ui.println("President: " + s.players().get(s.presidentIndex()).name()
                + " (#" + s.presidentIndex() + ")");
    }

    private int nominateChancellor(GameState s) {
        ui.println("Players:");
        s.players().forEach(p -> ui.println("  " + p.id() + " = " + p.name()));
        int idx;
        do {
            idx = ui.promptInt("President picks chancellor", 0, s.players().size() - 1);
            if (idx == s.presidentIndex()) {
                ui.println("  cannot pick yourself, try again");
            }
        } while (idx == s.presidentIndex());
        return idx;
    }

    private boolean collectVotes(GameState s) {
        long yes = s.players().stream()
                .map(p -> ui.promptYesNo(p.name() + " votes"))
                .filter(v -> v)
                .count();
        long no = s.players().size() - yes;
        ui.println("Result: " + yes + " Ja / " + no + " Nein");
        return yes > no;
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
