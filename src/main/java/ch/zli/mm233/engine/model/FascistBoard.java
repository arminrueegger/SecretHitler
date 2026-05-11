package ch.zli.mm233.engine.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FascistBoard {

    private static final List<ExecutivePower> BOARD_5_6 = Collections.unmodifiableList(Arrays.asList(
            null,
            null,
            ExecutivePower.POLICY_PEEK,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    ));

    private static final List<ExecutivePower> BOARD_7_8 = Collections.unmodifiableList(Arrays.asList(
            null,
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.CALL_SPECIAL_ELECTION,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    ));

    private static final List<ExecutivePower> BOARD_9_10 = Collections.unmodifiableList(Arrays.asList(
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.CALL_SPECIAL_ELECTION,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    ));

    private FascistBoard() {}

    public static List<ExecutivePower> forPlayerCount(int playerCount) {
        return switch (playerCount) {
            case 5, 6 -> BOARD_5_6;
            case 7, 8 -> BOARD_7_8;
            case 9, 10 -> BOARD_9_10;
            default -> throw new IllegalArgumentException("playerCount must be 5..10, got " + playerCount);
        };
    }
}
