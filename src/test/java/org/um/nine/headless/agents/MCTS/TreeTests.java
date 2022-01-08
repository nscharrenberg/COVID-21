package org.um.nine.headless.agents.MCTS;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.mcts.Actions;
import org.um.nine.headless.agents.mcts.Node;
import org.um.nine.headless.agents.state.State;

public class TreeTests {
    @Test
    public void rootTest() {
        Node root = new Node(new State());
        Assertions.assertEquals(root.isRoot(),true);
        Node child = new Node(new State(), root, Actions.DRIVE);
        Assertions.assertEquals(child.isRoot(),false);
        Assertions.assertEquals(child.getParent().isRoot(),true);
    }

    @Test
    public void childTests(){
        Node root = new Node(new State());
        Assertions.assertEquals(root.getChildren().isEmpty(),true);
        Node child1 = new Node(new State(), root, Actions.DRIVE);
        Node child2 = new Node(new State(), root, Actions.DRIVE);
        Node child3 = new Node(new State(), root, Actions.DRIVE);
        Assertions.assertEquals(root.getChildren().contains(child1),true);
        Assertions.assertEquals(root.getChildren().contains(child2),true);
        Assertions.assertEquals(root.getChildren().contains(child3),true);
    }

    @Test
    public void leafTest(){
        Node root = new Node(new State());
        Assertions.assertEquals(root.isLeaf(),true);
        Node child = new Node(new State(), root, Actions.DRIVE);
        Assertions.assertEquals(root.isLeaf(),false);
        Assertions.assertEquals(child.isLeaf(),true);
    }
}
