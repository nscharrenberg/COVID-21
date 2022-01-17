package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;

import java.util.HashMap;
import java.util.Map;

import static org.um.nine.headless.game.Settings.DEFAULT_REPORTER;
import static org.um.nine.headless.game.Settings.LOG;

public class ExperimentalGame implements IReportable {
    private final Map<IState, MacroNode[]> actions;
    private final IState currentState;
    private static int INCREMENT = 0;
    private final int id;


    public ExperimentalGame(IState state) {
        this.currentState = state;
        this.actions = new HashMap<>();
        this.id = INCREMENT++;
        if (LOG) {
            this.setPath(REPORT_PATH[0] + "/game-" + this.getId());
            DEFAULT_REPORTER.reportInitialState(this.currentState, "/initial-state-report.txt");
        }
    }

    public ExperimentalGame() {
        this(GameStateFactory.createInitialState());
    }

    public Map<IState, MacroNode[]> getActionsHistory() {
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
