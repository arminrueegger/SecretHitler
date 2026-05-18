package ch.zli.mm233.engine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record GameState(
        List<Player> players,
        List<ExecutivePower> executiveActions,
        int liberalPolicies,
        int fascistPolicies,
        List<Policy> drawPile,
        List<Policy> discardPile,
        int electionTracker,
        int presidentIndex,
        Integer lastElectedPresident,
        Integer lastElectedChancellor,
        Set<Integer> investigatedPlayerIds,
        Integer specialElectionReturnIndex,
        boolean vetoUnlocked,
        Phase phase,
        WinCondition winner,
        PendingAction pendingAction
) {
    public GameState {
        players               = List.copyOf(Objects.requireNonNull(players, "players"));
        executiveActions      = Collections.unmodifiableList(new ArrayList<>(
                                    Objects.requireNonNull(executiveActions, "executiveActions")));
        drawPile              = List.copyOf(Objects.requireNonNull(drawPile, "drawPile"));
        discardPile           = List.copyOf(Objects.requireNonNull(discardPile, "discardPile"));
        investigatedPlayerIds = Set.copyOf(Objects.requireNonNull(investigatedPlayerIds, "investigatedPlayerIds"));
        Objects.requireNonNull(phase, "phase");
    }
}
