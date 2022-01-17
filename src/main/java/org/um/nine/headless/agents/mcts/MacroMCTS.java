package org.um.nine.headless.agents.mcts;

import org.nd4j.common.primitives.Atomic;
import org.nd4j.common.primitives.AtomicDouble;
import org.um.nine.headless.agents.Agent;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MacroMCTS {

    private MacroNode root;
    private int maxIterations;
    private boolean endState = false;
    //important for the selection step. higher C = higher exploration
    private final double C = 0.5;

    private Logger log = new Logger();

    //all possible actions
    private final Actions[] moves = {Actions.DRIVE,Actions.CHARTER_FLIGHT,Actions.SHUTTLE,Actions.BUILD_RESEARCH_STATION,Actions.TREAT_DISEASE,Actions.SHARE_KNOWLEDGE,Actions.DISCOVER_CURE,Actions.ROLE_ACTION};

    public MacroMCTS(IState state, int iterations){
        root = new MacroNode(state);
        maxIterations = iterations;
        if(iterations == 0) maxIterations = Integer.MAX_VALUE;
    }

    /**
     * UCB selection: Selects a node that will be explored
     * @return the node that will be selected
     */
    public MacroNode selection(MacroNode node){
        LinkedList<MacroNode> children = node.getChildren();
        double[] scores = new double[children.size()];
        //finding min and max for normalization
        AtomicReference<Double> min = new AtomicReference<>((double) Integer.MAX_VALUE);
        AtomicReference<Double> max = new AtomicReference<>((double) Integer.MIN_VALUE);
        children.forEach(c -> {
            if(c.getValue() < min.get()) min.set(c.getValue());
            if(c.getValue() > max.get()) max.set(c.getValue());
        });
        max.set(max.get()-min.get());

        int maxIndex = 0;
        for(int i = 0; i < children.size(); i++){
            //normalized value
            double value = (node.getValue() - min.get())/max.get();
            int parentVisits;
            if(node.getParent() == null) parentVisits = 0;
            else parentVisits = node.getParent().getVisits();
            //ucb formula
            scores[i] = value + C*Math.sqrt(Math.log(parentVisits)/(double) node.getVisits());
            if(scores[i] > scores[maxIndex]){
                maxIndex = i;
            }
        }

        return children.get(maxIndex);
    }

    /**
     * Adding the child nodes for every possible action
     * @param current = current node
     */
    public void expand(MacroNode current){
        HPAMacroActionsFactory factory = HPAMacroActionsFactory.init(current.state, current.state.getPlayerRepository().getCurrentPlayer().getCity(), current.state.getPlayerRepository().getCurrentPlayer());
        List<MacroAction> macroActions = factory.getActions();
        for (MacroAction a: macroActions) {
            try{
                IState s = simulate(a, current.getState());
                //adds node as child
                new MacroNode(s, a, current);
            }
            catch (Exception ignored){
            }
        }
    }

    /**
     * simulates a move in a given state
     * @param a is the action that will be simulated
     * @param current is the current state that will be changed
     * @return the next State after the move
     * @throws MoveNotPossibleException if a move is not possible this will be thrown
     */
    public IState simulate(MacroAction a, IState current) throws MoveNotPossibleException {
        IState nextState = current.clone();
        MacroActionsExecutor executor = new MacroActionsExecutor();
        try{
            executor.executeIndexedMacro(current,a,false);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return nextState;
    }

    /**
     * backpropogates from leaf to root and changes values of the nodes
     */
    public void backPropagation(){
        ArrayList<MacroNode> leaves = new ArrayList<>();
        findLeaves(root, leaves);
        leaves.forEach( node -> {
            double value = node.getValue();
            int amtNodes = 1;
            MacroNode current = node;
            while(!current.getParent().isRoot()){
                current = current.getParent();
                value += current.getValue();
                amtNodes++;
            }
            value /= amtNodes;
            current.setValue(value);
        });
    }

    /**
     * Recursively finds the leaves of a tree
     * @param node The root node
     * @param leaves The arraylist the leaves will be saved in
     */
    private void findLeaves(MacroNode node, ArrayList<MacroNode> leaves){
        if(node.isLeaf()) leaves.add(node);
        else{
            node.getChildren().forEach(n -> {
                findLeaves(n,leaves);
            });
        }
    }

    public MacroAction run(IState current){

        root = new MacroNode(current);
        int iterations = 0;
        endState = false;
        //simulation
        while(iterations < maxIterations-1 && !endState){
            //selection step
            MacroNode currentNode = root;
            while(!currentNode.isLeaf()){
                currentNode = selection(currentNode);
                currentNode.increaseVisits();
            }
            //expansion step
            expand(currentNode);

            //backpropagation
            backPropagation();

            //checking for game specific events, like the draw and infect phase
            if(currentNode.getState().isGameLost() || currentNode.getState().isGameWon()) {
                endState = true;
            }
            if(currentNode.getDepth()%4 == 0 && currentNode.getDepth() != 0){
                //cloning the player decks and shuffling them for the simulation
                Stack<PlayerCard> simulationPD = (Stack<PlayerCard>) currentNode.getState().getCardRepository().getPlayerDeck().clone();
                Stack<InfectionCard> simulationID = (Stack<InfectionCard>) currentNode.getState().getCardRepository().getInfectionDeck().clone();
                Collections.shuffle(simulationPD);
                Collections.shuffle(simulationID);
                currentNode.getState().getPlayerRepository().getCurrentPlayer().addHand(simulationPD.pop());
                currentNode.getState().getPlayerRepository().getCurrentPlayer().addHand(simulationPD.pop());

                for(int i = 0; i < Objects.requireNonNull(currentNode.getState().getDiseaseRepository().getInfectionRates().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount(); i++){
                    InfectionCard ic = simulationID.pop();
                    try {
                        currentNode.getState().getDiseaseRepository().infect(ic.getCity().getColor(), ic.getCity());
                    } catch (NoCubesLeftException | NoDiseaseOrOutbreakPossibleDueToEvent | GameOverException e) {
                        e.printStackTrace();
                    }
                }
                currentNode.getState().getPlayerRepository().nextPlayer();
            }
            iterations++;
        }
        //playing the highest valued node
        AtomicDouble highestVal = new AtomicDouble();
        highestVal.set(Integer.MIN_VALUE);
        Atomic<MacroNode> node = new Atomic<>();
        root.getChildren().forEach(n -> {
            if(highestVal.get() < n.getValue()){
                highestVal.set(n.getValue());
                node.set(n);
            }
        });

        return node.get().getMacro();
    }

    public Logger getLog() {
        return log;
    }

    public MacroNode getRoot(){
        return root;
    }

    private class MacroNode implements Serializable {
        private int visits;
        private double value;
        private LinkedList<MacroNode> children;
        private MacroNode parent;
        private final int depth;
        private boolean isRoot;
        private IState state;
        private MacroAction macro;

        public MacroNode(IState state) {
            this.state = state;
            isRoot = true;
            depth = 0;
            visits = 1;
            value = 0;
            children = new LinkedList<>();
            evaluate();
        }

        public MacroNode(IState state, MacroAction macro, MacroNode parent) {
            this.state = state;
            this.macro = macro;
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
                value += 2 * red.get();
                value += 2 * black.get();
                value += 2 * blue.get();
                value += 2 * yellow.get();
            });
            double[] numberOfOutbreaks = new double[1];
            state.getDiseaseRepository().getOutbreakMarkers().forEach(outbreakMarker -> {
                if (outbreakMarker.isCurrent())
                    numberOfOutbreaks[0] = outbreakMarker.getId();
                value -= 3 * numberOfOutbreaks[0];
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
                value += 2 * red.get();
                value += 2 * black.get();
                value += 2 * blue.get();
                value += 2 * yellow.get();
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

        public LinkedList<MacroNode> getChildren() {
            return children;
        }

        public int getDepth() {
            return depth;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public MacroNode getParent() {
            return parent;
        }

        public IState getState() {
            return state;
        }

        public void setState(IState state) {
            this.state = state;
        }

        public MacroAction getMacro() {
            return macro;
        }

        public void setMacro(MacroAction macro) {
            this.macro = macro;
        }
    }
}
