package org.um.nine.headless.agents.mcts;

import org.nd4j.common.primitives.Atomic;
import org.nd4j.common.primitives.AtomicDouble;
import org.um.nine.headless.agents.Agent;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.Log;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class MCTS implements Agent {

    private Node root;
    private int maxIterations;
    private boolean endState = false;
    //important for the selection step. higher C = higher exploration
    private final double C = 0.5;

    private Log log = new Log();

    //all possible actions
    private final Actions[] moves = {Actions.DRIVE,Actions.CHARTER_FLIGHT,Actions.SHUTTLE,Actions.BUILD_RESEARCH_STATION,Actions.TREAT_DISEASE,Actions.SHARE_KNOWLEDGE,Actions.DISCOVER_CURE,Actions.ROLE_ACTION};

    public MCTS(IState state, int iterations){
        root = new Node(state);
        maxIterations = iterations;
        if(iterations == 0) maxIterations = Integer.MAX_VALUE;
    }

    /**
     * UCB selection: Selects a node that will be explored
     * @return the node that will be selected
     */
    public Node selection(Node node){
        LinkedList<Node> children = node.getChildren();
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
                    ArrayList<Player> copy = new ArrayList<>();
                    copy.addAll(player.getCity().getPawns());
                    copy.remove(player);
                    int rnd = new Random().nextInt(copy.size());
                    copy.get(rnd).addHand(sameCityCard.get());
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
                        case RED -> red.set(playerCards.size());
                        case BLACK -> black.set(playerCards.size());
                        case BLUE -> blue.set(playerCards.size());
                        case YELLOW -> yellow.set(playerCards.size());
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
     * backpropogates from leaf to root and changes values of the nodes
     */
    public void backPropagation(){
        ArrayList<Node> leaves = new ArrayList<>();
        findLeaves(root, leaves);
        leaves.forEach( node -> {
            double value = node.getValue();
            int amtNodes = 1;
            Node current = node;
            while(!current.getParent().isRoot()){
                current = current.getParent();
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

    public Actions run(IState current){

        root = new Node(current);
        int iterations = 0;
        endState = false;
        //simulation
        while(iterations < maxIterations-1 && !endState){
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
        Atomic<Actions> action = new Atomic<>();
        root.getChildren().forEach(n -> {
            if(highestVal.get() < n.getValue()){
                highestVal.set(n.getValue());
                action.set(n.getActions());
            }
        });

        System.out.println(action.get());
        return action.get();
    }

    /**
     * Runs the mcts and executes it.
     * @param current the current state
     * @return the next state
     * @throws MoveNotPossibleException if the action cannot be performed
     */
    public void agentDecision(IState current) throws MoveNotPossibleException {
        Actions a = run(current);
        Player player = current.getPlayerRepository().getCurrentPlayer();
        switch(a){
            case DRIVE -> {
                City next = player.getCity().getNeighbors().get(new Random().nextInt(player.getCity().getNeighbors().size() - 1));
                try {
                    current.getPlayerRepository().drive(player, next, true);
                    log.addStep("drive",next,player);
                }catch(Exception e){
                    throw new MoveNotPossibleException();
                }
            }
            case CHARTER_FLIGHT -> {
                int rnd = new Random().nextInt(current.getCityRepository().getCities().size() - 1);
                ArrayList<City> temp = new ArrayList<>(current.getCityRepository().getCities().values());
                try {
                    current.getPlayerRepository().charter(player, temp.get(rnd));
                    log.addStep("charter",temp.get(rnd),player);
                } catch (Exception e) {
                    throw new MoveNotPossibleException();
                }
            }
            case  DIRECT_FLIGHT -> {
                CityCard cityCard = (CityCard) player.getHand().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                if (cityCard!= null){
                    current.getBoardRepository().setSelectedCity(cityCard.getCity());
                    try{
                        current.getPlayerRepository().direct(player, cityCard.getCity());
                        log.addStep("direct", cityCard.getCity(), player);
                    }catch(Exception e){
                        throw new MoveNotPossibleException();
                    }
                }
                else {
                    throw new MoveNotPossibleException();
                }
            }
            case SHUTTLE -> {
                if (current.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null).count() >= 2 &&
                        player.getCity().getResearchStation() != null) {
                    City city = current.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                    if (city != null) {
                        current.getBoardRepository().setSelectedCity(city);
                        try {
                            current.getPlayerRepository().shuttle(player, city);
                            log.addStep("shuttle",city,player);
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
                        current.getPlayerRepository().buildResearchStation(player, player.getCity());
                        log.addStep("research station", player.getCity(), player);
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
                        current.getPlayerRepository().treat(player, player.getCity(), player.getCity().getCubes().get(0).getColor());
                        log.addStep("treat", player.getCity(), player);

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
                    ArrayList<Player> copy = new ArrayList<>();
                    copy.addAll(player.getCity().getPawns());
                    copy.remove(player);
                    int rnd = new Random().nextInt(copy.size());
                    copy.get(rnd).addHand(sameCityCard.get());
                    log.addStep("share", player.getCity(), player);
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
                        case RED -> red.set(playerCards.size());
                        case BLACK -> black.set(playerCards.size());
                        case BLUE -> blue.set(playerCards.size());
                        case YELLOW -> yellow.set(playerCards.size());
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
                    Optional<Cure> cure = current.getDiseaseRepository().getCures().values().stream().filter(c -> {
                        return c.getColor().equals(finalColor);
                    }).findFirst();
                    if (cure.isPresent()) {
                        try {
                            current.getDiseaseRepository().discoverCure(player, cure.get());
                            log.addStep(" discovered cure", player.getCity(), player);
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
                throw new MoveNotPossibleException();
            }
        }
    }

    @Override
    public Log getLog() {
        return log;
    }

    public Node getRoot(){
        return root;
    }
}
