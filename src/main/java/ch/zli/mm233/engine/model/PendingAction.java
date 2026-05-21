package ch.zli.mm233.engine.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public sealed interface PendingAction
        permits PendingAction.Election,
                PendingAction.LegislativeSession,
                PendingAction.ExecutiveActionPending {

    record Election(
            Integer chancellorCandidateIndex,
            Map<Integer, Boolean> votes
    ) implements PendingAction {
        public Election {
            votes = Map.copyOf(Objects.requireNonNull(votes, "votes"));
        }
    }

    record LegislativeSession(
            int electedChancellorIndex,
            List<Policy> presidentHand,
            List<Policy> chancellorHand,
            boolean vetoProposed
    ) implements PendingAction {
        public LegislativeSession {
            presidentHand = List.copyOf(Objects.requireNonNull(presidentHand, "presidentHand"));
            chancellorHand = List.copyOf(Objects.requireNonNull(chancellorHand, "chancellorHand"));
        }
    }

    record ExecutiveActionPending(ExecutivePower power) implements PendingAction {
        public ExecutiveActionPending {
            Objects.requireNonNull(power, "power");
        }
    }
}
