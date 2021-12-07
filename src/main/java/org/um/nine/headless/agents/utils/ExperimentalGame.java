package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalGame {
    private final List<MacroAction> actions;
    private final IState initialState;
    private final IState currentState;

    public ExperimentalGame() {
        this.initialState = GameStateFactory.createInitialState();
        this.currentState = this.initialState;
        this.actions = new ArrayList<>();
    }

    public void start() {
        this.initialState.getBoardRepository().reset();
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
