package ch.zli.mm233.session;

import ch.zli.mm233.engine.model.GameState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameSession {
    private GameState current;
    // empty for now
    private List<GameState> history = List.of();
}
