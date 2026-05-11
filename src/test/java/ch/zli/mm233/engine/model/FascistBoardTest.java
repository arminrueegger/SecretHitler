package ch.zli.mm233.engine.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FascistBoardTest {

    @Test
    void fiveSixBoard() {
        assertThat(FascistBoard.forPlayerCount(5)).isEqualTo(Arrays.asList(
                null,
                null,
                ExecutivePower.POLICY_PEEK,
                ExecutivePower.EXECUTION,
                ExecutivePower.EXECUTION));
        assertThat(FascistBoard.forPlayerCount(6)).isEqualTo(FascistBoard.forPlayerCount(5));
    }

    @Test
    void sevenEightBoard() {
        assertThat(FascistBoard.forPlayerCount(7)).isEqualTo(Arrays.asList(
                null,
                ExecutivePower.INVESTIGATE_LOYALTY,
                ExecutivePower.CALL_SPECIAL_ELECTION,
                ExecutivePower.EXECUTION,
                ExecutivePower.EXECUTION));
        assertThat(FascistBoard.forPlayerCount(8)).isEqualTo(FascistBoard.forPlayerCount(7));
    }

    @Test
    void nineTenBoard() {
        assertThat(FascistBoard.forPlayerCount(9)).isEqualTo(Arrays.asList(
                ExecutivePower.INVESTIGATE_LOYALTY,
                ExecutivePower.INVESTIGATE_LOYALTY,
                ExecutivePower.CALL_SPECIAL_ELECTION,
                ExecutivePower.EXECUTION,
                ExecutivePower.EXECUTION));
        assertThat(FascistBoard.forPlayerCount(10)).isEqualTo(FascistBoard.forPlayerCount(9));
    }

    @Test
    void returnedListIsImmutable() {
        List<ExecutivePower> board = FascistBoard.forPlayerCount(9);
        assertThatThrownBy(() -> board.set(0, null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsInvalidPlayerCount() {
        assertThatThrownBy(() -> FascistBoard.forPlayerCount(4))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> FascistBoard.forPlayerCount(11))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
