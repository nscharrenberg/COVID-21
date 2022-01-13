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
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.exceptions.GameOverException;

import java.io.File;
import java.io.IOException;

import static org.um.nine.headless.game.Settings.*;

public class ExperimentalGameRunner {

    public static void main(String[] args) {
        assert HEADLESS;
        assert DEFAULT_INITIAL_STATE;


        String gamePath = IReportable.REPORT_PATH[0];

        try {
            File file = new File(gamePath);
            if (file.exists()) FileUtils.cleanDirectory(file);

        } catch (IOException e) {
            e.printStackTrace();
        }


        DEFAULT_MACRO_ACTIONS_EXECUTOR = new MacroActionsExecutor();
        DEFAULT_MUTATOR = new Mutator();


        int n_rep = 10;
        for (int i = 0; i < n_rep; i++) {
            DEFAULT_RUNNING_GAME = new ExperimentalGame(GameStateFactory.createInitialState());
            IState state = DEFAULT_RUNNING_GAME.getCurrentState();
            try {
                for (Player player : state.getPlayerRepository().getPlayers().values()) {
                    state.getPlayerRepository().setCurrentPlayer(player);
                    IAgent rhAgent = new Individual(new MacroAction[5]).initGenome(state);
                    MacroAction nextMacro = rhAgent.getNextMacroAction(state);
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, nextMacro, true);
                }
            } catch (GameOverException gameOver) {
                System.out.println(gameOver.getMessage());
                DEFAULT_RUNNING_GAME.setPath(gamePath);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(IReportable.REPORT_PATH[0]);
            }
        }


    }


}
