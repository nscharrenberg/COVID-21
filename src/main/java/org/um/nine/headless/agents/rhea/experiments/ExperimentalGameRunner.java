package org.um.nine.headless.agents.rhea.experiments;

import org.apache.commons.io.FileUtils;
import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.core.Mutator;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.GameWonException;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    public static void main(String[] args) throws Exception {
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
        System.exit(0);


        //initialise constants
        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor();
        DEFAULT_MUTATOR = new Mutator();


        int n_rep = 1;
        gamesLoop:
        for (int i = 0; i < n_rep; i++) {

            // initialise the game
            DEFAULT_REPORTER.clear();

            DEFAULT_RUNNING_GAME = new ExperimentalGame();
            IState gameState = DEFAULT_RUNNING_GAME.getCurrentState();
            String gamePath = IReportable.REPORT_PATH[0];


            IAgent[] agents = new IAgent[4];
            IntStream.range(0, DEFAULT_PLAYERS.size()).forEach(k -> agents[k] = new Individual(new MacroAction[5]));


            while (DEFAULT_RUNNING_GAME.onGoing()) {

                DEFAULT_RUNNING_GAME.setPath(gamePath);
                MacroNode[] allPlayersMacro = new MacroNode[DEFAULT_PLAYERS.size()];

                // for each player
                for (int k = 0; k < DEFAULT_PLAYERS.size(); k++) {

                    DEFAULT_RUNNING_GAME.setPath(gamePath + "/" + DEFAULT_PLAYERS.get(k));
                    gameState.getPlayerRepository().setCurrentPlayer(DEFAULT_PLAYERS.get(k));
                    MacroNode macroNode = null;
                    try {
                        // apply evolutionary algorithm to get the best macro
                        MacroAction nextMacro = agents[k].getNextMacroAction(gameState);
                        DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(gameState, nextMacro, true);
                        // if no exceptions arise then we can keep the macro
                        macroNode = new MacroNode(DEFAULT_PLAYERS.get(k), nextMacro);

                    } catch (GameOverException | GameWonException e) {
                        //System.err.println(e.getMessage() + " :: " + IReportable.getDescription());
                        // if neither the mutation was successful just break the game and start a new one
                        continue gamesLoop;
                    } catch (Exception e) {
                        System.err.println(e.getMessage() + " :: " + IReportable.getDescription());
                        e.printStackTrace();
                    } finally {
                        DEFAULT_REPORTER.setPath(reportPath);
                    }
                    // finally, store the successful mutation macro
                    if (macroNode != null) allPlayersMacro[k] = macroNode;

                }
                // add to the game history the state and the 4 macro to apply with the default player order
                DEFAULT_RUNNING_GAME.getActionsHistory().put(gameState.clone(), allPlayersMacro);

            }
        }


    }


}
