package org.um.nine.experiments.hybrid;

import org.apache.commons.io.FileUtils;
import org.um.nine.experiments.rhea.graph.StatGraph;
import org.um.nine.headless.agents.hybrid.hybrid;
import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.experiments.MacroNode;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.Marker;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.GameWonException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.headless.game.repositories.PlayerRepository;

import java.io.File;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentRunnerHybrid {
    public static void main(String[] args) {
        StatGraph actionTypeGraph = new StatGraph("Actions Stats");
        actionTypeGraph.pack();
        actionTypeGraph.setVisible(true);

        runExperiments();
    }

    private static void runExperiments() {
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


        //initialise constants
        ITERATIONS = 4;


        int n_rep = 10;
        gamesLoop:
        for (int i = 0; i < n_rep; i++) {

            // initialise the game
            DEFAULT_REPORTER.clear();

            DEFAULT_RUNNING_GAME = new ExperimentalGame();
            IState gameState = DEFAULT_RUNNING_GAME.getCurrentState();
            String gamePath = IReportable.REPORT_PATH[0];

            GameStateFactory.getAnalyticsRepository().start(gameState);


            gameRunning:
            while (DEFAULT_RUNNING_GAME.onGoing()) {

                DEFAULT_RUNNING_GAME.setPath(gamePath);
                MacroNode[] allPlayersMacro = new MacroNode[DEFAULT_PLAYERS.size()];
                PlayerRepository.ignored = true;

                // for each player
                for (int k = 0; k < DEFAULT_PLAYERS.size(); k++) {

                    DEFAULT_RUNNING_GAME.setPath(gamePath + "/" + DEFAULT_PLAYERS.get(k));
                    gameState.getPlayerRepository().setCurrentPlayer(DEFAULT_PLAYERS.get(k));
                    hybrid h = new hybrid();
                    try{
                        MacroAction action = h.run(gameState);
                        DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(gameState, action, true);

                    } catch (EmptyStackException e){
                        GameStateFactory.getAnalyticsRepository().lost();
                        break gameRunning;
                    }catch (GameOverException e) {
                        GameStateFactory.getAnalyticsRepository().lost();
                        break gameRunning;
                    } catch (GameWonException e) {
                        GameStateFactory.getAnalyticsRepository().won();
                        break gameRunning;
                    }catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        DEFAULT_REPORTER.setPath(reportPath);
                    }

                }
                // add to the game history the state and the 4 macro to apply with the default player order
                DEFAULT_RUNNING_GAME.getActionsHistory().put(gameState.clone(), allPlayersMacro);
            }


            final int[] stateIndex = {0};
            DEFAULT_RUNNING_GAME.getActionsHistory().forEach(
                    (state, macroNodes) -> {

                        DEFAULT_REPORTER.clear();
                        DEFAULT_REPORTER.setPath(gamePath + "/state-" + stateIndex[0] + ".txt");
                        DEFAULT_REPORTER.logState(state).forEach(DEFAULT_REPORTER::append);
                        DEFAULT_REPORTER.append("\n\n");
                        DEFAULT_REPORTER.append("Round " + stateIndex[0]);

                        for (MacroNode macroNode : macroNodes) {
                            DEFAULT_REPORTER.append("Player " + macroNode.player());
                            DEFAULT_REPORTER.append("Macro " + macroNode.macroAction());
                        }
                        DEFAULT_REPORTER.report();
                        DEFAULT_REPORTER.clear();
                        stateIndex[0]++;
                    }
            );

            DEFAULT_REPORTER.setPath(reportPath);
        }
    }
}
