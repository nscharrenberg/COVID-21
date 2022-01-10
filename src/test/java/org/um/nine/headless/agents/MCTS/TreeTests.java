package org.um.nine.headless.agents.MCTS;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.mcts.Actions;
import org.um.nine.headless.agents.mcts.Node;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.state.State;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.game.exceptions.PlayerLimitException;

public class TreeTests {
    @Test
    public void rootTest() {
        ExperimentalGame game = new ExperimentalGame();
        try{
            game.getCurrentState().getPlayerRepository().createPlayer("P1",true);
            game.getCurrentState().getPlayerRepository().createPlayer("P2",true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        game.start();
        IState state = game.getCurrentState();

        Node root = new Node(state);
        Assertions.assertEquals(root.isRoot(),true);
        Node child = new Node(state, root, Actions.DRIVE);
        Assertions.assertEquals(child.isRoot(),false);
        Assertions.assertEquals(child.getParent().isRoot(),true);
    }

    @Test
    public void childTests(){
        ExperimentalGame game = new ExperimentalGame();
        try{
            game.getCurrentState().getPlayerRepository().createPlayer("P1",true);
            game.getCurrentState().getPlayerRepository().createPlayer("P2",true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        game.start();
        IState state = game.getCurrentState();
        Node root = new Node(state);
        Assertions.assertEquals(root.getChildren().isEmpty(),true);
        Node child1 = new Node(state, root, Actions.DRIVE);
        Node child2 = new Node(state, root, Actions.DRIVE);
        Node child3 = new Node(state, root, Actions.DRIVE);
        Assertions.assertEquals(root.getChildren().contains(child1),true);
        Assertions.assertEquals(root.getChildren().contains(child2),true);
        Assertions.assertEquals(root.getChildren().contains(child3),true);
    }

    @Test
    public void leafTest(){
        ExperimentalGame game = new ExperimentalGame();
        try{
            game.getCurrentState().getPlayerRepository().createPlayer("P1",true);
            game.getCurrentState().getPlayerRepository().createPlayer("P2",true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        game.start();
        IState state = game.getCurrentState();
        Node root = new Node(state);
        Assertions.assertEquals(root.isLeaf(),true);
        Node child = new Node(state, root, Actions.DRIVE);
        Assertions.assertEquals(root.isLeaf(),false);
        Assertions.assertEquals(child.isLeaf(),true);
    }
}
