package ch.zli.mm233.engine.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FascistBoardTest {

    @Test
    void fiveSixBoardSlots() {
        assertThat(FascistBoard.powerFor(5, 1)).isNull();
        assertThat(FascistBoard.powerFor(5, 2)).isNull();
        assertThat(FascistBoard.powerFor(5, 3)).isEqualTo(ExecutivePower.POLICY_PEEK);
        assertThat(FascistBoard.powerFor(6, 4)).isEqualTo(ExecutivePower.EXECUTION);
        assertThat(FascistBoard.powerFor(6, 5)).isEqualTo(ExecutivePower.EXECUTION);
    }

    @Test
    void sevenEightBoardSlots() {
        assertThat(FascistBoard.powerFor(7, 1)).isNull();
        assertThat(FascistBoard.powerFor(7, 2)).isEqualTo(ExecutivePower.INVESTIGATE_LOYALTY);
        assertThat(FascistBoard.powerFor(8, 3)).isEqualTo(ExecutivePower.CALL_SPECIAL_ELECTION);
        assertThat(FascistBoard.powerFor(7, 4)).isEqualTo(ExecutivePower.EXECUTION);
        assertThat(FascistBoard.powerFor(8, 5)).isEqualTo(ExecutivePower.EXECUTION);
    }

    @Test
    void nineTenBoardSlots() {
        assertThat(FascistBoard.powerFor(9, 1)).isEqualTo(ExecutivePower.INVESTIGATE_LOYALTY);
        assertThat(FascistBoard.powerFor(10, 2)).isEqualTo(ExecutivePower.INVESTIGATE_LOYALTY);
        assertThat(FascistBoard.powerFor(9, 3)).isEqualTo(ExecutivePower.CALL_SPECIAL_ELECTION);
        assertThat(FascistBoard.powerFor(10, 4)).isEqualTo(ExecutivePower.EXECUTION);
        assertThat(FascistBoard.powerFor(9, 5)).isEqualTo(ExecutivePower.EXECUTION);
    }

    @Test
    void rejectsInvalidPlayerCount() {
        assertThatThrownBy(() -> FascistBoard.powerFor(4, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> FascistBoard.powerFor(11, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidSlot() {
        assertThatThrownBy(() -> FascistBoard.powerFor(7, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> FascistBoard.powerFor(7, 6))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
