package org.um.nine.headless.agents.baseline;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;


public class baselineAgentTests {

    @Test
    public void decisionsTest() {
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        Logger log = state.getPlayerRepository().getLog();
        BaselineAgent ba = new BaselineAgent();
        ba.randomAction(state.getPlayerRepository().getCurrentPlayer(), state);
        int size = log.getLog().size();
        System.out.println("Log: " + size);
        System.out.println(log);
        Assertions.assertEquals(state.getPlayerRepository().getCurrentPlayer().getName(), log.getLog().get(size - 1).split("\t")[0]);
        Assertions.assertEquals(state.getPlayerRepository().getCurrentPlayer().getCity().getName(), log.getLog().get(size - 1).split("\t")[2]);
    }

    @Test
    public void repeatedDecisionsTest() {
        for (int i = 0; i < 1000; i++) {
            decisionsTest();
        }
    }
}
