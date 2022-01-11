package org.um.nine.headless.agents.baseline;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.agents.utils.Log;
import org.um.nine.headless.game.exceptions.PlayerLimitException;


public class baselineAgentTests {

    @Test
    public void decisionsTest() {
        ExperimentalGame game = new ExperimentalGame();
        try{
            game.getCurrentState().getPlayerRepository().createPlayer("P1",true);
            game.getCurrentState().getPlayerRepository().createPlayer("P2",true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        game.start();
        IState state = game.getCurrentState();
        Log log = state.getPlayerRepository().getLog();
        BaselineAgent ba = new BaselineAgent();
        ba.agentDecision(state);
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
