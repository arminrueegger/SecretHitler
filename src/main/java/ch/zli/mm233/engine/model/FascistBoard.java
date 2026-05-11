package ch.zli.mm233.engine.model;

public final class FascistBoard {

    private static final ExecutivePower[] BOARD_5_6 = {
            null,
            null,
            ExecutivePower.POLICY_PEEK,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    };

    private static final ExecutivePower[] BOARD_7_8 = {
            null,
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.CALL_SPECIAL_ELECTION,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    };

    private static final ExecutivePower[] BOARD_9_10 = {
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.INVESTIGATE_LOYALTY,
            ExecutivePower.CALL_SPECIAL_ELECTION,
            ExecutivePower.EXECUTION,
            ExecutivePower.EXECUTION
    };

    private FascistBoard() {}

    public static ExecutivePower powerFor(int playerCount, int fascistSlot) {
        if (fascistSlot < 1 || fascistSlot > 5) {
            throw new IllegalArgumentException("fascistSlot must be 1..5, got " + fascistSlot);
        }
        return board(playerCount)[fascistSlot - 1];
    }

    private static ExecutivePower[] board(int playerCount) {
        return switch (playerCount) {
            case 5, 6 -> BOARD_5_6;
            case 7, 8 -> BOARD_7_8;
            case 9, 10 -> BOARD_9_10;
            default -> throw new IllegalArgumentException("playerCount must be 5..10, got " + playerCount);
        };
    }
}
