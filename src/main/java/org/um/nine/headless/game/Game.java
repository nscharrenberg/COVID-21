package org.um.nine.headless.game;

import org.um.nine.headless.game.domain.actions.macro.MacroAction;
import org.um.nine.headless.game.domain.state.IState;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<MacroAction> actions;
    private final IState initialState;
    private IState currentState;
    private boolean onGoing;

    public Game(boolean bySettings) {
        this.initialState = GameStateFactory.createInitialState();
        this.currentState = this.initialState;
        this.actions = new ArrayList<>();
        if (bySettings) this.configureSettings();
        this.onGoing = true;
    }

    public void configureSettings() {
        try {
            for (int i = 0; i< Settings.BOT_PLAYERS; i++)
                this.initialState.getPlayerRepository().createPlayer("Test"+(i+1),true);

            this.initialState.getBoardRepository().setDifficulty(Settings.DIFFICULTY);
            this.initialState.getBoardRepository().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public boolean onGoing() {return this.onGoing && !this.currentState.isGameLost() && !this.currentState.isGameWon();}

    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
    }

    public void setCurrentState(IState currentState) {
        this.currentState = currentState;
    }

}
