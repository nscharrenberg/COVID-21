package org.um.nine.headless.agents.mcts;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Marker;
import org.um.nine.headless.game.domain.cards.CityCard;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Node {
    private int visits;
    private double value;
    private LinkedList<Node> children;
    private Node parent;
    private final int depth;
    private boolean isRoot;
    private IState state;
    private Actions actions;

    public Node(IState state) {
        this.state = state;
        isRoot = true;
        depth = 0;
        visits = 1;
        value = 0;
        children = new LinkedList<>();
        evaluate();
    }

    public Node(IState state, Node parent, Actions actions) {
        this.actions = actions;
        this.state = state;
        this.parent = parent;
        parent.getChildren().add(this);
        depth = parent.getDepth() + 1;
        visits = 1;
        value = 0;
        children = new LinkedList<>();
        evaluate();
    }

    // todo evaluate on creation
    private void evaluate() {
        // until Ilian does his stuff
        value = 1000;
        state.getCityRepository().getCities().values().forEach(c -> {
            value -= 10 * c.getCubes().size();
        });

        state.getPlayerRepository().getPlayers().values().forEach(p -> {
            var sameColorCard = p.getHand().stream().filter(c -> c instanceof CityCard)
                    .collect(Collectors.groupingBy(c -> ((CityCard) c).getCity().getColor()));
            AtomicInteger red = new AtomicInteger();
            AtomicInteger black = new AtomicInteger();
            AtomicInteger blue = new AtomicInteger();
            AtomicInteger yellow = new AtomicInteger();
            sameColorCard.forEach((color, playerCards) -> {
                switch (color) {
                    case RED -> red.getAndIncrement();
                    case BLACK -> black.getAndIncrement();
                    case BLUE -> blue.getAndIncrement();
                    case YELLOW -> yellow.getAndIncrement();
                }
            });

            value += 3 * p.getHand().size();
        });

        state.getPlayerRepository().getPlayers().values().forEach(s -> {
            var sameColorCard = s.getHand().stream().filter(c -> c instanceof CityCard)
                    .collect(Collectors.groupingBy(c -> ((CityCard) c).getCity().getColor()));
            AtomicInteger red = new AtomicInteger();
            AtomicInteger black = new AtomicInteger();
            AtomicInteger blue = new AtomicInteger();
            AtomicInteger yellow = new AtomicInteger();
            sameColorCard.forEach((color, playerCards) -> {
                switch (color) {
                    case RED -> red.getAndIncrement();
                    case BLACK -> black.getAndIncrement();
                    case BLUE -> blue.getAndIncrement();
                    case YELLOW -> yellow.getAndIncrement();
                }
            });
            if (red.get() == 1)
                value += 1;
            else if (red.get() == 2)
                value += 2;
            else if (red.get() == 3)
                value += 3;
            else if (red.get() == 4)
                value += 4;
            else
                value += 5;

            if (black.get() == 1)
                value += 1;
            else if (black.get() == 2)
                value += 2;
            else if (black.get() == 3)
                value += 3;
            else if (black.get() == 4)
                value += 4;
            else
                value += 5;

            if (blue.get() == 1)
                value += 1;
            else if (blue.get() == 2)
                value += 2;
            else if (blue.get() == 3)
                value += 3;
            else if (blue.get() == 4)
                value += 4;
            else
                value += 5;

            if (yellow.get() == 1)
                value += 1;
            else if (yellow.get() == 2)
                value += 2;
            else if (yellow.get() == 3)
                value += 3;
            else if (yellow.get() == 4)
                value += 4;
            else
                value += 5;
        });
        double[] numberOfOutbreaks = new double[1];
        state.getDiseaseRepository().getOutbreakMarkers().forEach(outbreakMarker -> {
            if (outbreakMarker.isCurrent())
                numberOfOutbreaks[0] = outbreakMarker.getId();
            value -= 3 * numberOfOutbreaks[0];
        });

        long numberOfDiscoveredCures = state.getDiseaseRepository().getCures().values().stream()
                .filter(Cure::isDiscovered).count();
        value += 100 * (double) numberOfDiscoveredCures;

        long numberOfResearchstations = state.getCityRepository().getCities().values().stream()
                .filter(c -> c.getResearchStation() != null).count();
        value += 2 * (double) numberOfResearchstations;

        double infectionRate = Objects.requireNonNull(this.state.getDiseaseRepository().getInfectionRates().stream()
                .filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
        value -= 100 * infectionRate;

        if (state.isGameWon())
            value = Integer.MAX_VALUE;
        if (state.isGameLost())
            value = Integer.MIN_VALUE;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public void increaseVisits() {
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
