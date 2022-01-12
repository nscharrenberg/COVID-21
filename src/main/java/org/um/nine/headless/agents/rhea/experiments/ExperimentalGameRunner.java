package org.um.nine.headless.agents.rhea.experiments;

import org.apache.commons.io.FileUtils;
import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.core.Mutator;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;

import java.io.File;
import java.io.IOException;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    public static void main(String[] args) {
        assert HEADLESS;
        assert DEFAULT_INITIAL_STATE;


        try {
            File file = new File(IReportable.REPORT_PATH[0]);
            if (file.exists()) FileUtils.cleanDirectory(file);

        } catch (IOException e) {
            e.printStackTrace();
        }


        DEFAULT_RUNNING_GAME = new ExperimentalGame(GameStateFactory.createInitialState());
        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor(DEFAULT_RUNNING_GAME);
        DEFAULT_MUTATOR = new Mutator(DEFAULT_RUNNING_GAME);
        IState state = DEFAULT_RUNNING_GAME.getCurrentState();


        IAgent rhAgent = new Individual(new MacroAction[5]).initGenome(state);
        MacroAction nextMacro = rhAgent.getNextMacroAction(state);
        DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, nextMacro, true);
        /*

        try {
            while (DEFAULT_RUNNING_GAME.onGoing()) {
                for (Player p : state.getPlayerRepository().getPlayerOrder()) {
                    state.getPlayerRepository().setCurrentPlayer(p);
                    IAgent rhAgent = new Individual(new MacroAction[5]).initGenome(state);
                    MacroAction nextMacro = rhAgent.getNextMacroAction(state);
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, nextMacro, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         */

    }


}
