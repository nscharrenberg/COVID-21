package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.game.Settings.LOG;

public class ExperimentalGame implements IReportable {
    private final List<MacroAction> actions;
    private final IState currentState;
    private static int INCREMENT = 0;
    private final int id;

    public ExperimentalGame(IState state) {
        this.currentState = state;
        this.actions = new ArrayList<>();
        this.id = INCREMENT++;
        if (LOG) this.setPath(REPORT_PATH[0] + "/game-" + this.getId());
    }

    public ExperimentalGame() {
        this(GameStateFactory.createInitialState());
    }

    public List<MacroAction> getActionsHistory() {
        return actions;
    }

    public IState getCurrentState() {
        return this.currentState;
    }

    public boolean onGoing() {
        return !this.currentState.isGameLost() &&
                !this.currentState.isGameWon();
    }

    public int getId() {
        return id;
    }
}
