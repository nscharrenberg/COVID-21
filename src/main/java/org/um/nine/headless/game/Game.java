package org.um.nine.headless.game;

import org.um.nine.headless.game.domain.actions.macro.MacroAction;
import org.um.nine.headless.game.domain.state.IState;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<MacroAction> actions;
    private final IState initialState;
    private IState currentState;

    public Game() {
        this.initialState = GameStateFactory.createInitialState();
        this.currentState = this.initialState;
        this.actions = new ArrayList<>();
    }

    public void start() {
        this.initialState.getBoardRepository().start();
    }
    public List<MacroAction> getActionsHistory() {
        return actions;
    }
    public IState getInitialState() {
        return this.initialState;
    }
    public IState getCurrentState() {
        return this.currentState;
    }
    public boolean onGoing() {
        return  !this.currentState.isGameLost() &&
                !this.currentState.isGameWon();
    }

}
