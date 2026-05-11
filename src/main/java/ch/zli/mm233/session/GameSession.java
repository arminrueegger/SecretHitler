package ch.zli.mm233.session;

import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.PendingAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameSession {
    private GameState current;
    // empty for now
    private List<GameState> history = List.of();
    private PendingAction pending;
}
