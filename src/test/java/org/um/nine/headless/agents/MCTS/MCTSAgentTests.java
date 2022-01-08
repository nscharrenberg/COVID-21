package org.um.nine.headless.agents.MCTS;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.mcts.Node;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.game.exceptions.PlayerLimitException;

import java.sql.SQLOutput;

public class MCTSAgentTests {

    @Test
    public void expansionTest(){
        ExperimentalGame game = new ExperimentalGame();
        try{
            game.getCurrentState().getPlayerRepository().createPlayer("P1",true);
            game.getCurrentState().getPlayerRepository().createPlayer("P2",true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        game.start();
        IState state = game.getCurrentState();
        MCTS mcts = new MCTS(state,1);
        mcts.expand(mcts.getRoot());
        mcts.getRoot().getChildren().forEach(c -> {
            System.out.println(c.getActions().toString());
        });
        assert(mcts.getRoot().getChildren().size() >= 1);
    }

    @Test
    public void simulationTest(){

    }

}
