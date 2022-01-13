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
import org.um.nine.headless.game.exceptions.GameOverException;

import java.io.File;
import java.io.IOException;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    public static void main(String[] args) {
        assert HEADLESS;
        assert DEFAULT_INITIAL_STATE;

        String reportPath = IReportable.REPORT_PATH[0];


        try {
            File file = new File(reportPath);
            if (file.exists()) FileUtils.cleanDirectory(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //FIXME: UNCOMMENT AND RUN BEFORE GIT COMMITTING
        //System.exit(0);


        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor();
        DEFAULT_MUTATOR = new Mutator();


        int n_rep = 10;
        for (int i = 0; i < n_rep; i++) {
            DEFAULT_RUNNING_GAME = new ExperimentalGame(GameStateFactory.createInitialState());
            IState state = DEFAULT_RUNNING_GAME.getCurrentState();

            String gamePath = IReportable.REPORT_PATH[0];
            IAgent[] agents = new IAgent[4];
            for (int k = 0; k < DEFAULT_PLAYERS.size(); k++) {
                IState initState = state.getClonedState();
                initState.getPlayerRepository().setCurrentPlayer(DEFAULT_PLAYERS.get(k));
                agents[k] = new Individual(new MacroAction[5]).initGenome(initState);
            }

            DEFAULT_RUNNING_GAME.setPath(gamePath);


            for (int k = 0; k < DEFAULT_PLAYERS.size(); k++) {
                IState mutationState = state.getClonedState();
                mutationState.getPlayerRepository().setCurrentPlayer(DEFAULT_PLAYERS.get(k));
                try {
                    MacroAction nextMacro = agents[k].getNextMacroAction(mutationState);
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(mutationState, nextMacro, true);
                } catch (GameOverException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DEFAULT_RUNNING_GAME.setPath(gamePath);
            }

            DEFAULT_RUNNING_GAME.setPath(reportPath);
        }


    }


}
