package ch.zli.mm233.engine;

import ch.zli.mm233.engine.model.FascistBoard;
import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.Party;
import ch.zli.mm233.engine.model.Phase;
import ch.zli.mm233.engine.model.Player;
import ch.zli.mm233.engine.model.Policy;
import ch.zli.mm233.engine.model.Role;
import ch.zli.mm233.engine.model.WinCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GameEngine {

    private static final Random RNG = new Random();
    private static final int LIBERAL_WIN = 5;
    private static final int FASCIST_WIN = 6;
    private static final int LIBERAL_POLICY_COUNT = 6;
    private static final int FASCIST_POLICY_COUNT = 11;

    private GameEngine() {}

    public record DrawResult(GameState state, List<Policy> drawn) {
        public DrawResult {
            drawn = List.copyOf(drawn);
        }
    }

    public static GameState newGame(List<String> names) {
        int n = names.size();
        if (n < 5 || n > 10) {
            throw new IllegalArgumentException("playerCount must be 5..10, got " + n);
        }
        int fascCount = fascistsFor(n);
        int libCount = n - fascCount - 1;

        List<Role> roles = Stream.of(
                Collections.nCopies(libCount, Role.LIBERAL),
                Collections.nCopies(fascCount, Role.FASCIST),
                List.of(Role.HITLER)
        ).flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(roles, RNG);

        List<Player> players = IntStream.range(0, n)
                .mapToObj(i -> new Player(
                        i,
                        names.get(i),
                        roles.get(i),
                        roles.get(i) == Role.LIBERAL ? Party.LIBERAL : Party.FASCIST,
                        true))
                .toList();

        List<Policy> deck = Stream.of(
                Collections.nCopies(LIBERAL_POLICY_COUNT, Policy.LIBERAL),
                Collections.nCopies(FASCIST_POLICY_COUNT, Policy.FASCIST)
        ).flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(deck, RNG);

        return new GameState(
                players,
                FascistBoard.forPlayerCount(n),
                0, 0,
                deck,
                List.of(),
                0, 0,
                null, null,
                Set.of(),
                null,
                false,
                Phase.ELECTION,
                null
        );
    }

    public static GameState failedElection(GameState s) {
        int newTracker = s.electionTracker() + 1;
        if (newTracker >= 3) {
            return enactChaosPolicy(s);
        }

        return rebuildWithTracker(s,
                s.liberalPolicies(),
                s.fascistPolicies(),
                s.drawPile(),
                s.discardPile(),
                newTracker,
                nextPresident(s),
                Phase.ELECTION,
                s.winner());
    }

    private static GameState enactChaosPolicy(GameState s) {
        boolean needsReshuffle = s.drawPile().isEmpty();
        List<Policy> sourceDeck = needsReshuffle
                ? reshuffledDeck(s.drawPile(), s.discardPile())
                : s.drawPile();
        List<Policy> discard = needsReshuffle ? List.of() : s.discardPile();

        Policy enacted = sourceDeck.getFirst();
        List<Policy> remainingDraw = sourceDeck.subList(1, sourceDeck.size());

        int newLib = enacted == Policy.LIBERAL ? s.liberalPolicies() + 1 : s.liberalPolicies();
        int newFasc = enacted == Policy.FASCIST ? s.fascistPolicies() + 1 : s.fascistPolicies();

        WinCondition winner = newLib >= LIBERAL_WIN ? WinCondition.LIBERAL_POLICIES
                : newFasc >= FASCIST_WIN ? WinCondition.FASCIST_POLICIES
                : null;
        Phase phase = winner != null ? Phase.GAME_OVER : Phase.ELECTION;
        int nextPres = winner != null ? s.presidentIndex() : nextPresident(s);

        return new GameState(
                s.players(),
                s.executiveActions(),
                newLib,
                newFasc,
                remainingDraw,
                discard,
                0,
                nextPres,
                null,
                null,
                s.investigatedPlayerIds(),
                s.specialElectionReturnIndex(),
                newFasc >= 5 || s.vetoUnlocked(),
                phase,
                winner
        );
    }

    public static DrawResult drawThree(GameState s) {
        boolean needsReshuffle = s.drawPile().size() < 3;
        List<Policy> sourceDeck = needsReshuffle
                ? reshuffledDeck(s.drawPile(), s.discardPile())
                : s.drawPile();
        List<Policy> remainingDiscard = needsReshuffle ? List.of() : s.discardPile();

        List<Policy> drawn = sourceDeck.subList(0, 3);
        List<Policy> remainingDraw = sourceDeck.subList(3, sourceDeck.size());

        GameState newState = rebuild(s,
                s.liberalPolicies(),
                s.fascistPolicies(),
                remainingDraw,
                remainingDiscard,
                s.presidentIndex(),
                s.phase(),
                s.winner());
        return new DrawResult(newState, drawn);
    }

    public static GameState enactPolicy(GameState s, Policy enacted, Policy presDiscard, Policy chancDiscard) {
        int newLib = enacted == Policy.LIBERAL ? s.liberalPolicies() + 1 : s.liberalPolicies();
        int newFasc = enacted == Policy.FASCIST ? s.fascistPolicies() + 1 : s.fascistPolicies();
        WinCondition winner = newLib >= LIBERAL_WIN ? WinCondition.LIBERAL_POLICIES
                : newFasc >= FASCIST_WIN ? WinCondition.FASCIST_POLICIES
                : null;
        Phase phase = winner != null ? Phase.GAME_OVER : Phase.ELECTION;
        int nextPres = winner != null ? s.presidentIndex() : nextPresident(s);

        List<Policy> newDiscard = Stream.concat(
                s.discardPile().stream(),
                Stream.of(presDiscard, chancDiscard)
        ).toList();

        return rebuildWithTracker(s, newLib, newFasc, s.drawPile(), newDiscard, 0, nextPres, phase, winner);
    }

    private static int fascistsFor(int playerCount) {
        return switch (playerCount) {
            case 5, 6 -> 1;
            case 7, 8 -> 2;
            case 9, 10 -> 3;
            default -> throw new IllegalArgumentException("playerCount must be 5..10, got " + playerCount);
        };
    }

    private static int nextPresident(GameState s) {
        return (s.presidentIndex() + 1) % s.players().size();
    }

    private static List<Policy> reshuffledDeck(List<Policy> draw, List<Policy> discard) {
        List<Policy> combined = new ArrayList<>(draw.size() + discard.size());
        combined.addAll(draw);
        combined.addAll(discard);
        Collections.shuffle(combined, RNG);
        return combined;
    }

    private static GameState rebuild(
            GameState s,
            int liberalPolicies,
            int fascistPolicies,
            List<Policy> drawPile,
            List<Policy> discardPile,
            int presidentIndex,
            Phase phase,
            WinCondition winner
    ) {
        return rebuildWithTracker(s, liberalPolicies, fascistPolicies, drawPile, discardPile,
                s.electionTracker(), presidentIndex, phase, winner);
    }

    private static GameState rebuildWithTracker(
            GameState s,
            int liberalPolicies,
            int fascistPolicies,
            List<Policy> drawPile,
            List<Policy> discardPile,
            int electionTracker,
            int presidentIndex,
            Phase phase,
            WinCondition winner
    ) {
        return new GameState(
                s.players(),
                s.executiveActions(),
                liberalPolicies,
                fascistPolicies,
                drawPile,
                discardPile,
                electionTracker,
                presidentIndex,
                s.lastElectedPresident(),
                s.lastElectedChancellor(),
                s.investigatedPlayerIds(),
                s.specialElectionReturnIndex(),
                s.vetoUnlocked(),
                phase,
                winner
        );
    }
}
