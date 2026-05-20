package ch.zli.mm233.engine;

import ch.zli.mm233.engine.model.ExecutivePower;
import ch.zli.mm233.engine.model.FascistBoard;
import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.Party;
import ch.zli.mm233.engine.model.PendingAction;
import ch.zli.mm233.engine.model.Phase;
import ch.zli.mm233.engine.model.Player;
import ch.zli.mm233.engine.model.Policy;
import ch.zli.mm233.engine.model.Role;
import ch.zli.mm233.engine.model.WinCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                null,
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

    public static GameState nominateChancellor(GameState s, int candidateIndex) {
        if (s.phase() != Phase.ELECTION) {
            throw new IllegalStateException("Can only nominate during ELECTION phase");
        }
        if (candidateIndex < 0 || candidateIndex >= s.players().size()) {
            throw new IllegalArgumentException("Invalid candidate index: " + candidateIndex);
        }
        if (candidateIndex == s.presidentIndex()) {
            throw new IllegalArgumentException("President cannot nominate themselves");
        }
        Player candidate = s.players().get(candidateIndex);
        if (!candidate.alive()) {
            throw new IllegalArgumentException("Cannot nominate dead player");
        }
        if (!isEligibleChancellor(s, candidateIndex)) {
            throw new IllegalArgumentException("Player is ineligible due to term limits");
        }

        PendingAction.Election election = new PendingAction.Election(candidateIndex, Map.of());
        return withPendingAction(s, election);
    }

    public static GameState castVote(GameState s, int playerIndex, boolean ja) {
        if (!(s.pendingAction() instanceof PendingAction.Election election)) {
            throw new IllegalStateException("No election in progress");
        }
        if (playerIndex < 0 || playerIndex >= s.players().size()) {
            throw new IllegalArgumentException("Invalid player index: " + playerIndex);
        }
        Player voter = s.players().get(playerIndex);
        if (!voter.alive()) {
            throw new IllegalArgumentException("Dead players cannot vote");
        }
        if (election.votes().containsKey(playerIndex)) {
            throw new IllegalArgumentException("Player has already voted");
        }

        Map<Integer, Boolean> newVotes = new HashMap<>(election.votes());
        newVotes.put(playerIndex, ja);
        PendingAction.Election updatedElection = new PendingAction.Election(
                election.chancellorCandidateIndex(), newVotes);

        return withPendingAction(s, updatedElection);
    }

    public static boolean allVotesCast(GameState s) {
        if (!(s.pendingAction() instanceof PendingAction.Election election)) {
            return false;
        }
        long aliveCount = s.players().stream().filter(Player::alive).count();
        return election.votes().size() == aliveCount;
    }

    public static GameState resolveElection(GameState s) {
        if (!(s.pendingAction() instanceof PendingAction.Election election)) {
            throw new IllegalStateException("No election to resolve");
        }
        if (!allVotesCast(s)) {
            throw new IllegalStateException("Not all votes have been cast");
        }

        long jaVotes = election.votes().values().stream().filter(v -> v).count();
        long neinVotes = election.votes().size() - jaVotes;
        boolean passed = jaVotes > neinVotes;

        if (!passed) {
            return failedElection(clearPendingAction(s));
        }

        int chancellorIndex = election.chancellorCandidateIndex();
        Player chancellor = s.players().get(chancellorIndex);

        if (s.fascistPolicies() >= 3 && chancellor.role() == Role.HITLER) {
            return new GameState(
                    s.players(),
                    s.executiveActions(),
                    s.liberalPolicies(),
                    s.fascistPolicies(),
                    s.drawPile(),
                    s.discardPile(),
                    s.electionTracker(),
                    s.presidentIndex(),
                    s.presidentIndex(),
                    chancellorIndex,
                    s.investigatedPlayerIds(),
                    s.specialElectionReturnIndex(),
                    s.vetoUnlocked(),
                    Phase.GAME_OVER,
                    WinCondition.HITLER_ELECTED_CHANCELLOR,
                    null
            );
        }

        return new GameState(
                s.players(),
                s.executiveActions(),
                s.liberalPolicies(),
                s.fascistPolicies(),
                s.drawPile(),
                s.discardPile(),
                s.electionTracker(),
                s.presidentIndex(),
                s.presidentIndex(),
                chancellorIndex,
                s.investigatedPlayerIds(),
                s.specialElectionReturnIndex(),
                s.vetoUnlocked(),
                Phase.LEGISLATIVE_SESSION,
                null,
                null
        );
    }

    public static boolean isEligibleChancellor(GameState s, int candidateIndex) {
        if (candidateIndex == s.presidentIndex()) {
            return false;
        }
        if (!s.players().get(candidateIndex).alive()) {
            return false;
        }
        long aliveCount = s.players().stream().filter(Player::alive).count();
        if (s.lastElectedChancellor() != null && s.lastElectedChancellor() == candidateIndex) {
            return false;
        }
        if (aliveCount >= 5 && s.lastElectedPresident() != null && s.lastElectedPresident() == candidateIndex) {
            return false;
        }
        return true;
    }

    private static GameState withPendingAction(GameState s, PendingAction action) {
        return new GameState(
                s.players(),
                s.executiveActions(),
                s.liberalPolicies(),
                s.fascistPolicies(),
                s.drawPile(),
                s.discardPile(),
                s.electionTracker(),
                s.presidentIndex(),
                s.lastElectedPresident(),
                s.lastElectedChancellor(),
                s.investigatedPlayerIds(),
                s.specialElectionReturnIndex(),
                s.vetoUnlocked(),
                s.phase(),
                s.winner(),
                action
        );
    }

    private static GameState clearPendingAction(GameState s) {
        return withPendingAction(s, null);
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
                winner,
                null
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

        List<Policy> newDiscard = Stream.concat(
                s.discardPile().stream(),
                Stream.of(presDiscard, chancDiscard)
        ).toList();

        if (winner != null) {
            return new GameState(
                    s.players(),
                    s.executiveActions(),
                    newLib,
                    newFasc,
                    s.drawPile(),
                    newDiscard,
                    0,
                    s.presidentIndex(),
                    s.lastElectedPresident(),
                    s.lastElectedChancellor(),
                    s.investigatedPlayerIds(),
                    s.specialElectionReturnIndex(),
                    newFasc >= 5 || s.vetoUnlocked(),
                    Phase.GAME_OVER,
                    winner,
                    null
            );
        }

        ExecutivePower power = enacted == Policy.FASCIST
                ? s.executiveActions().get(newFasc - 1)
                : null;

        if (power != null) {
            return new GameState(
                    s.players(),
                    s.executiveActions(),
                    newLib,
                    newFasc,
                    s.drawPile(),
                    newDiscard,
                    0,
                    s.presidentIndex(),
                    s.lastElectedPresident(),
                    s.lastElectedChancellor(),
                    s.investigatedPlayerIds(),
                    s.specialElectionReturnIndex(),
                    newFasc >= 5 || s.vetoUnlocked(),
                    Phase.EXECUTIVE_ACTION,
                    null,
                    new PendingAction.ExecutiveActionPending(power)
            );
        }

        return rebuildWithTracker(s, newLib, newFasc, s.drawPile(), newDiscard, 0, nextPresident(s), Phase.ELECTION, null);
    }

    // exekutivzühs
    public static GameState investigateLoyalty(GameState s, int targetPlayerIndex) {
        if (s.phase() != Phase.EXECUTIVE_ACTION) {
            throw new IllegalStateException("Can only investigate during EXECUTIVE_ACTION phase");
        }
        if (!(s.pendingAction() instanceof PendingAction.ExecutiveActionPending pending)
                || pending.power() != ExecutivePower.INVESTIGATE_LOYALTY) {
            throw new IllegalStateException("No investigate loyalty action pending");
        }
        if (targetPlayerIndex < 0 || targetPlayerIndex >= s.players().size()) {
            throw new IllegalArgumentException("Invalid target player index: " + targetPlayerIndex);
        }
        if (targetPlayerIndex == s.presidentIndex()) {
            throw new IllegalArgumentException("President cannot investigate themselves");
        }
        Player target = s.players().get(targetPlayerIndex);
        if (!target.alive()) {
            throw new IllegalArgumentException("Cannot investigate dead player");
        }
        if (s.investigatedPlayerIds().contains(targetPlayerIndex)) {
            throw new IllegalArgumentException("Player has already been investigated");
        }

        Set<Integer> newInvestigated = new java.util.HashSet<>(s.investigatedPlayerIds());
        newInvestigated.add(targetPlayerIndex);

        return transitionToElectionAfterExecutive(s, s.players(), newInvestigated);
    }

    public static Party getPlayerParty(GameState s, int playerIndex) {
        return s.players().get(playerIndex).partyCard();
    }

    public static GameState callSpecialElection(GameState s, int targetPlayerIndex) {
        if (!(s.pendingAction() instanceof PendingAction.ExecutiveActionPending pending)
                || pending.power() != ExecutivePower.CALL_SPECIAL_ELECTION) {
            throw new IllegalStateException("No special election action pending");
        }
        if (targetPlayerIndex < 0 || targetPlayerIndex >= s.players().size()) {
            throw new IllegalArgumentException("Invalid target player index: " + targetPlayerIndex);
        }
        if (targetPlayerIndex == s.presidentIndex()) {
            throw new IllegalArgumentException("President cannot pick themselves for special election");
        }
        Player target = s.players().get(targetPlayerIndex);
        if (!target.alive()) {
            throw new IllegalArgumentException("Cannot pick dead player for president");
        }

        int returnIndex = (s.presidentIndex() + 1) % s.players().size();

        return new GameState(
                s.players(),
                s.executiveActions(),
                s.liberalPolicies(),
                s.fascistPolicies(),
                s.drawPile(),
                s.discardPile(),
                s.electionTracker(),
                targetPlayerIndex,
                s.lastElectedPresident(),
                s.lastElectedChancellor(),
                s.investigatedPlayerIds(),
                returnIndex,
                s.vetoUnlocked(),
                Phase.ELECTION,
                null,
                null
        );
    }

    public static GameState policyPeek(GameState s) {

        boolean needsReshuffle = s.drawPile().size() < 3;
        List<Policy> sourceDeck = needsReshuffle
                ? reshuffledDeck(s.drawPile(), s.discardPile())
                : s.drawPile();
        List<Policy> remainingDiscard = needsReshuffle ? List.of() : s.discardPile();

        List<Policy> topThree = sourceDeck.subList(0, 3);

        return new GameState(
                s.players(),
                s.executiveActions(),
                s.liberalPolicies(),
                s.fascistPolicies(),
                sourceDeck,
                remainingDiscard,
                s.electionTracker(),
                s.presidentIndex(),
                s.lastElectedPresident(),
                s.lastElectedChancellor(),
                s.investigatedPlayerIds(),
                s.specialElectionReturnIndex(),
                s.vetoUnlocked(),
                Phase.EXECUTIVE_ACTION,
                null,
                new PendingAction.PolicyPeekResult(topThree)
        );
    }

    public static GameState acknowledgePeek(GameState s) {
        if (!(s.pendingAction() instanceof PendingAction.PolicyPeekResult)) {
            throw new IllegalStateException("falscher Zustand");
        }
        return transitionToElectionAfterExecutive(s, s.players(), s.investigatedPlayerIds());
    }
    public static GameState killPlayer(GameState s, int targetPlayerIndex) {
        if (s.phase() != Phase.EXECUTIVE_ACTION) {
            throw new IllegalStateException("Can only execute during executive_action phase");
        }
        if (!(s.pendingAction() instanceof PendingAction.ExecutiveActionPending pending)
                || pending.power() != ExecutivePower.EXECUTION) {
            throw new IllegalStateException("No execution action pending");
        }
        if (targetPlayerIndex < 0 || targetPlayerIndex >= s.players().size()) {
            throw new IllegalArgumentException("Invalid target player index:" + targetPlayerIndex);
        }
        Player target = s.players().get(targetPlayerIndex);
        if (!target.alive()) {
            throw new IllegalArgumentException("Cannot execute already dead player");
        }

        List<Player> newPlayers = new ArrayList<>(s.players());
        Player killed = newPlayers.get(targetPlayerIndex);
        newPlayers.set(targetPlayerIndex, new Player(killed.id(), killed.name(), killed.role(), killed.partyCard(), false));

        if (killed.role() == Role.HITLER) {
            return new GameState(
                    newPlayers,
                    s.executiveActions(),
                    s.liberalPolicies(),
                    s.fascistPolicies(),
                    s.drawPile(),
                    s.discardPile(),
                    s.electionTracker(),
                    s.presidentIndex(),
                    s.lastElectedPresident(),
                    s.lastElectedChancellor(),
                    s.investigatedPlayerIds(),
                    s.specialElectionReturnIndex(),
                    s.vetoUnlocked(),
                    Phase.GAME_OVER,
                    WinCondition.HITLER_EXECUTED,
                    null
            );
        }

        return transitionToElectionAfterExecutive(s, newPlayers, s.investigatedPlayerIds());
    }

    private static GameState transitionToElectionAfterExecutive(GameState s, List<Player> players, Set<Integer> investigated) {
        int nextPres;
        Integer newReturnIndex;
        if (s.specialElectionReturnIndex() != null) {
            nextPres = s.specialElectionReturnIndex();
            while (!players.get(nextPres).alive()) {
                nextPres = (nextPres + 1) % players.size();
            }
            newReturnIndex = null;
        } else {
            int next = (s.presidentIndex() + 1) % players.size();
            while (!players.get(next).alive()) {
                next = (next + 1) % players.size();
            }
            nextPres = next;
            newReturnIndex = null;
        }

        return new GameState(
                players,
                s.executiveActions(),
                s.liberalPolicies(),
                s.fascistPolicies(),
                s.drawPile(),
                s.discardPile(),
                s.electionTracker(),
                nextPres,
                s.lastElectedPresident(),
                s.lastElectedChancellor(),
                investigated,
                newReturnIndex,
                s.vetoUnlocked(),
                Phase.ELECTION,
                null,
                null
        );
    }

    //exekutiv züg

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
                winner,
                null
        );
    }
}
