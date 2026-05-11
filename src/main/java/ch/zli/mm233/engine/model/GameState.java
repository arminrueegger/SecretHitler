package ch.zli.mm233.engine.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record GameState(
        List<Player> players,
        int liberalPolicies,
        int fascistPolicies,
        List<Policy> drawPile,
        List<Policy> discardPile,
        int electionTracker,
        int presidentIndex,
        Integer chancellorCandidateIndex,
        Integer electedChancellorIndex,
        Integer lastElectedPresident,
        Integer lastElectedChancellor,
        List<Policy> presidentHand,
        List<Policy> chancellorHand,
        Map<Integer, Boolean> pendingVotes,
        ExecutivePower pendingPower,
        Set<Integer> investigatedPlayerIds,
        Integer specialElectionReturnIndex,
        boolean vetoUnlocked,
        boolean vetoProposed,
        Phase phase,
        WinCondition winner
) {
    public GameState {
        players               = List.copyOf(Objects.requireNonNull(players, "players"));
        drawPile              = List.copyOf(Objects.requireNonNull(drawPile, "drawPile"));
        discardPile           = List.copyOf(Objects.requireNonNull(discardPile, "discardPile"));
        presidentHand         = List.copyOf(Objects.requireNonNull(presidentHand, "presidentHand"));
        chancellorHand        = List.copyOf(Objects.requireNonNull(chancellorHand, "chancellorHand"));
        pendingVotes          = Map.copyOf(Objects.requireNonNull(pendingVotes, "pendingVotes"));
        investigatedPlayerIds = Set.copyOf(Objects.requireNonNull(investigatedPlayerIds, "investigatedPlayerIds"));
        Objects.requireNonNull(phase, "phase");
    }
}
