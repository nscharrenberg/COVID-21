package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.core.Mutator;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.Player;

import static org.um.nine.headless.agents.utils.Logger.*;
import static org.um.nine.headless.agents.utils.Reporter.report;
import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    public static void main(String[] args) {
        assert HEADLESS;
        assert DEFAULT_INITIAL_STATE;


        DEFAULT_RUNNING_GAME = new ExperimentalGame(GameStateFactory.createInitialState());

        if (LOG) {
            addLog("STARTING EXPERIMENT");
            addLog("Initializing new game state...");
            addLog(DEFAULT_RUNNING_GAME.getInitialState());
        }

        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor(DEFAULT_RUNNING_GAME);
        DEFAULT_MUTATOR = new Mutator(DEFAULT_RUNNING_GAME);

        IState state = DEFAULT_RUNNING_GAME.getInitialState();


        try {

            while (DEFAULT_RUNNING_GAME.onGoing()) {
                for (Player p : state.getPlayerRepository().getPlayerOrder()) {
                    state.getPlayerRepository().setCurrentPlayer(p);
                    IAgent rhAgent = new Individual(new MacroAction[5]);
                    MacroAction nextMacro = rhAgent.getNextMacroAction(state);
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, nextMacro, true);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log();
            report();
            clearLog();
        }


    }
}
