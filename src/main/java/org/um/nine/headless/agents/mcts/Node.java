package org.um.nine.headless.agents.mcts;

import org.um.nine.headless.agents.state.IState;
import java.util.LinkedList;

public class Node {
    private int visits;
    private double value;
    private LinkedList<Node> children;
    private Node parent;
    private final int depth;
    private boolean isRoot;
    private IState state;
    private Actions actions;

    public Node(IState state){
        this.state = state;
        isRoot = true;
        depth = 0;
        visits = 1;
        value = 0;
        children = new LinkedList<>();
        evaluate();
    }

    public Node(IState state,Node parent, Actions actions){
        this.actions = actions;
        this.state = state;
        this.parent = parent;
        parent.getChildren().add(this);
        depth = parent.getDepth()+1;
        visits = 1;
        value = 0;
        children = new LinkedList<>();
        evaluate();
    }

    //todo evaluate on creation
    private void evaluate(){
        //do evaluation stuff and give score
    }

    public boolean isLeaf(){
        return children.isEmpty();
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public void increaseVisits(){
        this.visits++;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LinkedList<Node> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Node getParent() {
        return parent;
    }

    public IState getState() {
        return state;
    }

    public void setState(IState state) {
        this.state = state;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
