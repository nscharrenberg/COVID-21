package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalGame {
    private final List<MacroAction> actions;
    private final IState initialState;
    private final IState currentState;

    public ExperimentalGame(IState state) {
        this.initialState = state;
        this.currentState = state.getClonedState();
        this.actions = new ArrayList<>();
    }

    public ExperimentalGame() {
        this.initialState = GameStateFactory.createInitialState();
        this.currentState = this.initialState.getClonedState();
        this.actions = new ArrayList<>();
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
        return !this.currentState.isGameLost() &&
                !this.currentState.isGameWon();
    }

}
