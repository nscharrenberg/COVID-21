package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.game.Settings.HEADLESS;

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

    public static class ExperimentalGameRunner {

        public static void main(String[] args) {
            if (HEADLESS) {
                ExperimentalGame game = new ExperimentalGame();
                MacroActionsExecutor experiment = new MacroActionsExecutor(game);
                experiment.runExperimentalGame();
            }
        }
    }
}
