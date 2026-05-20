package ch.zli.mm233.engine;

import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.Party;
import ch.zli.mm233.engine.model.PendingAction;
import ch.zli.mm233.engine.model.Phase;
import ch.zli.mm233.engine.model.Player;
import ch.zli.mm233.engine.model.Policy;
import ch.zli.mm233.engine.model.Role;
import ch.zli.mm233.engine.model.WinCondition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameEngineTest {

    private static List<Player> allLiberals(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> new Player(i, "P" + i, Role.LIBERAL, Party.LIBERAL, true))
                .toList();
    }

    private static List<Player> withDead(List<Player> ps, int... deadIds) {
        Set<Integer> dead = java.util.Arrays.stream(deadIds).boxed().collect(java.util.stream.Collectors.toUnmodifiableSet());
        return ps.stream()
                .map(p -> dead.contains(p.id())
                        ? new Player(p.id(), p.name(), p.role(), p.partyCard(), false)
                        : p)
                .toList();
    }

    private static List<Player> withHitler(List<Player> ps, int hitlerId) {
        return ps.stream()
                .map(p -> p.id() == hitlerId
                        ? new Player(p.id(), p.name(), Role.HITLER, Party.FASCIST, true)
                        : p)
                .toList();
    }

    private static GameState state(
            List<Player> players,
            int liberalPolicies,
            int fascistPolicies,
            int electionTracker,
            int presidentIndex,
            Integer lastPres,
            Integer lastChanc
    ) {
        List<Policy> deck = Stream.of(
                Collections.nCopies(3, Policy.LIBERAL),
                Collections.nCopies(3, Policy.FASCIST)
        ).flatMap(List::stream).toList();
        return new GameState(
                players,
                List.of(),
                liberalPolicies,
                fascistPolicies,
                deck,
                List.of(),
                electionTracker,
                presidentIndex,
                lastPres,
                lastChanc,
                Set.of(),
                null,
                false,
                Phase.ELECTION,
                null,
                null
        );
    }

    private static GameState voteAll(GameState afterNominate, boolean ja) {
        GameState cur = afterNominate;
        for (Player p : cur.players()) {
            if (p.alive()) {
                cur = GameEngine.castVote(cur, p.id(), ja);
            }
        }
        return cur;
    }

    @Test
    void newGame_dealsCorrectRoleCountsFor5Players() {
        GameState s = GameEngine.newGame(List.of("A", "B", "C", "D", "E"));
        long libs = s.players().stream().filter(p -> p.role() == Role.LIBERAL).count();
        long fascs = s.players().stream().filter(p -> p.role() == Role.FASCIST).count();
        long hitlers = s.players().stream().filter(p -> p.role() == Role.HITLER).count();
        assertThat(libs).isEqualTo(3);
        assertThat(fascs).isEqualTo(1);
        assertThat(hitlers).isEqualTo(1);
        assertThat(s.drawPile()).hasSize(17);
    }

    @Test
    void nominateChancellor_rejectsTermLimitedChancellor() {
        GameState s = state(allLiberals(5), 0, 0, 0, 0, null, 2);
        assertThatThrownBy(() -> GameEngine.nominateChancellor(s, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nominateChancellor_rejectsPresidentLockWith5PlusAlive() {
        GameState s = state(allLiberals(5), 0, 0, 0, 0, 3, null);
        assertThatThrownBy(() -> GameEngine.nominateChancellor(s, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nominateChancellor_allowsPresidentLockWhenLessThan5Alive() {
        List<Player> ps = withDead(allLiberals(5), 4);
        GameState s = state(ps, 0, 0, 0, 0, 3, null);
        GameState after = GameEngine.nominateChancellor(s, 3);
        assertThat(after.pendingAction()).isInstanceOf(PendingAction.Election.class);
    }

    @Test
    void resolveElection_hitlerAsChancellorAfter3FascistPolicies_endsGame() {
        List<Player> ps = withHitler(allLiberals(5), 2);
        GameState s = state(ps, 0, 3, 0, 0, null, null);
        GameState afterNominate = GameEngine.nominateChancellor(s, 2);
        GameState afterVotes = voteAll(afterNominate, true);

        GameState resolved = GameEngine.resolveElection(afterVotes);

        assertThat(resolved.phase()).isEqualTo(Phase.GAME_OVER);
        assertThat(resolved.winner()).isEqualTo(WinCondition.HITLER_ELECTED_CHANCELLOR);
    }

    @Test
    void resolveElection_failedVote_incrementsTracker() {
        GameState s = state(allLiberals(5), 0, 0, 0, 0, null, null);
        GameState afterNominate = GameEngine.nominateChancellor(s, 2);
        GameState afterVotes = voteAll(afterNominate, false);

        GameState resolved = GameEngine.resolveElection(afterVotes);

        assertThat(resolved.electionTracker()).isEqualTo(1);
        assertThat(resolved.phase()).isEqualTo(Phase.ELECTION);
        assertThat(resolved.pendingAction()).isNull();
    }

    @Test
    void failedElection_atTracker2_triggersChaosAndResets() {
        GameState s = state(allLiberals(5), 0, 0, 2, 0, 1, 2);
        int totalPoliciesBefore = s.liberalPolicies() + s.fascistPolicies();

        GameState after = GameEngine.failedElection(s);

        assertThat(after.electionTracker()).isEqualTo(0);
        assertThat(after.lastElectedPresident()).isNull();
        assertThat(after.lastElectedChancellor()).isNull();
        assertThat(after.liberalPolicies() + after.fascistPolicies())
                .isEqualTo(totalPoliciesBefore + 1);
    }
}
