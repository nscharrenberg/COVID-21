package org.um.nine.headless.agents;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.baseline.BaselineAgent;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.agents.utils.Log;


public class baselineAgentTests {

    @Test
    public void decisionsTest() {
        ExperimentalGame game = new ExperimentalGame();
        game.start();
        IState state = game.getCurrentState();
        Log log = state.getPlayerRepository().getLog();
        BaselineAgent ba = new BaselineAgent();
        ba.randomAction(state.getPlayerRepository().getCurrentPlayer(), state);
        int size = log.getLog().size();
        System.out.println("Log: " + size);
        System.out.println(log);
        Assertions.assertEquals(state.getPlayerRepository().getCurrentPlayer().getName(), log.getLog().get(size - 1).player().getName());
        Assertions.assertEquals(state.getPlayerRepository().getCurrentPlayer().getCity(), log.getLog().get(size - 1).targetLocation());
    }

    @Test
    public void repeatedDecisionsTest() {
        for (int i = 0; i < 1000; i++) {
            decisionsTest();
        }
    }
}
