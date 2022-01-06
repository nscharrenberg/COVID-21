package org.um.nine.headless.agents.mcts;

import org.nd4j.common.primitives.Atomic;
import org.nd4j.common.primitives.AtomicDouble;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.GameWonException;
import org.um.nine.headless.game.exceptions.MoveNotPossibleException;
import org.um.nine.headless.game.exceptions.UnableToDiscoverCureException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class MCTS {

    private Node root;
    private int maxIterations;
    private boolean endState = false;
    //important for the selection step. higher C = higher exploration
    private final double C = 0.5;

    //all possible actions
    private final Actions[] moves = {Actions.DRIVE,Actions.CHARTER_FLIGHT,Actions.SHUTTLE,Actions.BUILD_RESEARCH_STATION,Actions.TREAT_DISEASE,Actions.SHARE_KNOWLEDGE,Actions.DISCOVER_CURE,Actions.ROLE_ACTION};

    public MCTS(IState state, int iterations){
        root = new Node(state);
        maxIterations = iterations;
    }

    /**
     * UCB selection: Selects a node that will be explored
     * @return the node that will be selected
     */
    public Node selection(Node node){ //todo normalization
        LinkedList<Node> children = node.getChildren();
        double[] scores = new double[children.size()];
        int maxIndex = 0;
        for(int i = 0; i < children.size(); i++){
            //ucb formula
            scores[i] = node.getValue() + C*Math.sqrt(Math.log(node.getParent().getVisits())/(double) node.getVisits());
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
    public void expand(Node current){
        for (Actions a: moves) {
            try{
                IState s = simulate(a, current.getState());
                //adds node as child
                new Node(s, current, a);
            }
            catch (MoveNotPossibleException ignored){
            }
        }
    }

    //todo maybe add some logic for targeting certain cities (like prioritising infected cities)
    /**
     * simulates a move in a given state
     * @param a is the action that will be simulated
     * @param current is the current state that will be changed
     * @return the next State after the move
     * @throws MoveNotPossibleException if a move is not possible this will be thrown
     */
    public IState simulate(Actions a, IState current) throws MoveNotPossibleException {
        IState nextState = current.getClonedState();
        Player player = nextState.getPlayerRepository().getCurrentPlayer();
        switch(a){
            case DRIVE -> {
                City next = player.getCity().getNeighbors().get(new Random().nextInt(player.getCity().getNeighbors().size() - 1));
                try {
                    nextState.getPlayerRepository().drive(player, next, true);
                }catch(Exception e){
                    throw new MoveNotPossibleException();
                }
            }
            case CHARTER_FLIGHT -> {
                int rnd = new Random().nextInt(nextState.getCityRepository().getCities().size() - 1);
                ArrayList<City> temp = new ArrayList<>(nextState.getCityRepository().getCities().values());
                try {
                    nextState.getPlayerRepository().charter(player, temp.get(rnd));
                } catch (Exception e) {
                    throw new MoveNotPossibleException();
                }
            }
            case  DIRECT_FLIGHT -> {
                CityCard cityCard = (CityCard) player.getHand().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                if (cityCard!= null){
                    nextState.getBoardRepository().setSelectedCity(cityCard.getCity());
                    try{
                        nextState.getPlayerRepository().direct(player, cityCard.getCity());
                    }catch(Exception e){
                        throw new MoveNotPossibleException();
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case SHUTTLE -> {
                if (nextState.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null).count() >= 2 &&
                        player.getCity().getResearchStation() != null) {
                    City city = nextState.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                    if (city != null) {
                        nextState.getBoardRepository().setSelectedCity(city);
                        try {
                            nextState.getPlayerRepository().shuttle(player, city);
                        } catch (Exception e) {
                            throw new MoveNotPossibleException();
                        }
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case BUILD_RESEARCH_STATION -> {
                if (player.getCity().getResearchStation() == null && player.getHand().stream().filter(c -> {
                    if (c instanceof CityCard card) return card.getCity().equals(player.getCity());
                    return false;
                }).findFirst().orElse(null) != null) {
                    try{
                        nextState.getPlayerRepository().buildResearchStation(player, player.getCity());
                    }catch(Exception e){
                        throw new MoveNotPossibleException();
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case TREAT_DISEASE -> {
                if (!player.getCity().getCubes().isEmpty()) {
                    try {
                        nextState.getPlayerRepository().treat(player, player.getCity(), player.getCity().getCubes().get(0).getColor());
                    } catch (Exception e) {
                        throw new MoveNotPossibleException();
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case SHARE_KNOWLEDGE -> {
                Optional<PlayerCard> sameCityCard = player.getHand().stream().filter(c -> c instanceof CityCard && ((CityCard) c).getCity().equals(player.getCity())).findFirst();
                if(!sameCityCard.isPresent() || player.getCity().getPawns().size() <= 1){
                    throw new MoveNotPossibleException();
                }
                else{
                    player.getHand().remove(sameCityCard.get());
                    int rnd = new Random().nextInt(player.getCity().getPawns().size()-1);
                    player.getCity().getPawns().get(rnd).addHand(sameCityCard.get());
                }
            }
            case DISCOVER_CURE -> {
                var sameColorCard = player.getHand().stream().filter(c -> c instanceof CityCard).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                AtomicInteger red = new AtomicInteger();
                AtomicInteger black = new AtomicInteger();
                AtomicInteger blue = new AtomicInteger();
                AtomicInteger yellow = new AtomicInteger();
                sameColorCard.forEach((color, playerCards) -> {
                    switch (color){
                        case RED -> red.getAndIncrement();
                        case BLACK -> black.getAndIncrement();
                        case BLUE -> blue.getAndIncrement();
                        case YELLOW -> yellow.getAndIncrement();
                    }
                });
                Color color = Color.LIME;
                boolean curable = false;
                if(red.get() >= 5|| (player.getRole() instanceof Medic) && red.get() >=4){
                    curable = true;
                    color = Color.RED;
                }else if(black.get() >= 5|| (player.getRole() instanceof Medic) && black.get() >=4){
                    curable = true;
                    color = Color.BLACK;
                }else if(blue.get() >= 5|| (player.getRole() instanceof Medic) && blue.get() >=4){
                    curable = true;
                    color = Color.BLUE;
                }else if(yellow.get() >= 5|| (player.getRole() instanceof Medic) && yellow.get() >=4){
                    curable = true;
                    color = Color.YELLOW;
                }

                if (curable) {
                    Color finalColor = color;
                    Optional<Cure> cure = nextState.getDiseaseRepository().getCures().values().stream().filter(c -> {
                        return c.getColor().equals(finalColor);
                    }).findFirst();
                    if (cure.isPresent()) {
                        try {
                            nextState.getDiseaseRepository().discoverCure(player, cure.get());
                            nextState.getPlayerRepository().getLog().addStep(" discovered cure", player.getCity(), player);
                        } catch (UnableToDiscoverCureException | GameWonException e) {
                            throw new MoveNotPossibleException();
                        }
                    }
                    else {
                        throw new MoveNotPossibleException();
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case ROLE_ACTION -> {
                List<RoleAction> l = player.getRole().actions();
                if(l.size() == 0){
                    throw new MoveNotPossibleException();
                } else {
                    //todo add role logic here and remove exception
                    throw new MoveNotPossibleException();
                }
            }
        }
        return nextState;
    }

    /**
     * backpropogates from leaf to root and changes values of the nodes on the way
     */
    public void backPropagation(){
        ArrayList<Node> leaves = new ArrayList<>();
        findLeaves(root, leaves);
        leaves.forEach( node -> {
            double value = node.getValue();
            int amtNodes = 1;
            Node current = node;
            while(!current.getParent().isRoot()){
                current = node.getParent();
                value += current.getValue();
                amtNodes++;
            }
            //
            value /= amtNodes;
            current.setValue(value);
        });
    }

    /**
     * Recursively finds the leaves of a tree
     * @param node The root node
     * @param leaves The arraylist the leaves will be saved in
     */
    private void findLeaves(Node node, ArrayList<Node> leaves){
        if(node.isLeaf()) leaves.add(node);
        else{
            node.getChildren().forEach(n -> {
                findLeaves(n,leaves);
            });
        }
    }

    public Actions run(){
        int iterations = 0;
        endState = false;
        //simulation
        while(iterations < maxIterations && !endState){
            //selection step
            Node currentNode = root;
            while(!currentNode.isLeaf()){
                currentNode = selection(currentNode);
                currentNode.increaseVisits();
            }
            //expansion step
            expand(currentNode);

            //backpropagation
            backPropagation();

            iterations++;
        }
        //playing the highest valued node
        AtomicDouble highestVal = new AtomicDouble();
        highestVal.set(0);
        Atomic<Actions> action = new Atomic<>();
        root.getChildren().forEach(n -> {
            if(highestVal.get() < n.getValue()){
                highestVal.set(n.getValue());
                action.set(n.getActions());
            }
        });

        return action.get();
    }
}
