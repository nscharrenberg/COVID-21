package org.um.nine.headless.agents.mcts;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.Marker;

import java.util.LinkedList;
import java.util.Objects;

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
        //until Ilian does his stuff
        value = 1000;
        state.getCityRepository().getCities().values().forEach(c -> {
            value -= 10 * c.getCubes().size();
        });

        state.getPlayerRepository().getPlayers().values().forEach(p -> {
            value += 10 * p.getHand().size();
        });

        int infectionRate = Objects.requireNonNull(this.state.getDiseaseRepository().getInfectionRates().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
        value -= 100 * infectionRate;

        if(state.isGameWon()) value = Integer.MAX_VALUE;
        if(state.isGameLost()) value = Integer.MIN_VALUE;
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
