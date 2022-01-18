package org.um.nine.headless.agents.rhea.experiments;

import org.apache.commons.io.FileUtils;
import org.um.nine.experiments.rhea.graph.StatGraph;
import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.core.Mutator;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.GameWonException;
import org.um.nine.headless.game.repositories.PlayerRepository;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    static {
        StatGraph actionTypeGraph = new StatGraph("Actions Stats");
        actionTypeGraph.pack();
        actionTypeGraph.setVisible(true);
    }


    public static void main(String[] args) throws Exception {
        assert HEADLESS;
        assert DEFAULT_INITIAL_STATE;
        assert LOG;

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
        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor();
        DEFAULT_MUTATOR = new Mutator();


        int numGames = 3;
        int gamesWon = 0;
        gamesLoop:
        for (int i = 0; i < numGames; i++) {

            // initialise the game
            DEFAULT_REPORTER.clear();

            System.out.println("\n\nNew Game");
            DEFAULT_RUNNING_GAME = new ExperimentalGame();
            IState gameState = DEFAULT_RUNNING_GAME.getCurrentState();
            String gamePath = DEFAULT_REPORTER.REPORT_PATH[0];

            PlayerRepository.ignored = true;
            GameStateFactory.getAnalyticsRepository().start(gameState);


            IAgent[] agents = new IAgent[4];
            IntStream.range(0, DEFAULT_PLAYERS.size()).forEach(k -> agents[k] = new Individual(new MacroAction[ROLLING_HORIZON]));


            ROUND_INDEX = 0;
            gameRunningLoop:
            while (DEFAULT_RUNNING_GAME.onGoing()) {

                DEFAULT_REPORTER.setPath(gamePath);
                MacroNode[] allPlayersMacro = new MacroNode[DEFAULT_PLAYERS.size()];
                System.out.println("\nRound " + ROUND_INDEX);
                // for each player
                for (int k = 0; k < DEFAULT_PLAYERS.size(); k++) {

                    Player currentPlayer = DEFAULT_PLAYERS.get(k);
                    System.out.println("\nCurrent player : " + currentPlayer + " - city : " + currentPlayer.getCity().getName());
                    System.out.println("Cards in hand : " + currentPlayer.getHand().stream().map(cc -> ((CityCard) cc).getCity().getName()).collect(Collectors.toList()));
                    gameState.getPlayerRepository().setCurrentPlayer(currentPlayer);
                    MacroNode macroNode = null;
                    try {
                        // apply evolutionary algorithm to get the best macro
                        MacroAction nextMacro = agents[k].getNextMacroAction(gameState);
//                        MacroAction nextMacro = agents[k].getNextMacroAction(gameState).executableNow(gameState);
                        System.out.println("Executing actions :" + nextMacro + " ...");
                        DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(gameState, nextMacro, true);
                        // if no exceptions arise then we can keep the macro
                        macroNode = new MacroNode(DEFAULT_PLAYERS.get(k), nextMacro);
                    } catch (GameOverException e) {
                        //System.err.println(e.getMessage() + " :: " + IReportable.getDescription());
                        // if neither the mutation was successful just break the game and start a new one
                        GameStateFactory.getAnalyticsRepository().lost();
                        break gameRunningLoop;
                    } catch (GameWonException e) {
                        gamesWon++;
                        GameStateFactory.getAnalyticsRepository().won();
                        break gameRunningLoop;
                    } catch (Exception e) {
                        //System.err.println(e.getMessage() + " :: " + IReportable.getDescription());
                        e.printStackTrace();
                    } finally {
                        DEFAULT_REPORTER.setPath(reportPath);
                    }
                    // finally, store the successful mutation macro
                    if (macroNode != null) allPlayersMacro[k] = macroNode;

                }
                // add to the game history the state and the 4 macro to apply with the default player order
                DEFAULT_RUNNING_GAME.getActionsHistory().put(gameState.clone(), allPlayersMacro);
                ROUND_INDEX++;
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

            GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(gameState).summarize();
            DEFAULT_REPORTER.setPath(reportPath);
        }

        System.out.println("Games won : " + gamesWon);


    }


}
