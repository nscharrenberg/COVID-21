package org.um.nine.headless.agents.baseline;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.Agent;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class BaselineAgent implements Agent {

    private final boolean DEBUG = false;

    public BaselineAgent() {
    }

    public boolean moveToCity(Player currentPlayer, City target, IState state) throws InvalidMoveException {
        if (currentPlayer.getCity().getName().equals(target.getName())) return true;

        if (currentPlayer.getCity().getNeighbors().contains(target)) {
            state.getPlayerRepository().drive(currentPlayer, target, state, true);
            return true;
        }

        PlayerCard pc = currentPlayer.getHand().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(target);
            }

            return false;
        }).findFirst().orElse(null);

        if (pc != null) {
            currentPlayer.getHand().remove(pc);
            state.getPlayerRepository().drive(currentPlayer, target, state, false);
            return true;
        }


        /*
        TODO: else find an algorithm to calc shortest/lightest path to get to the target (also in many rounds)
        */

        return false;
    }

    public boolean followPlayer(Player currentPlayer, Player toFollow, IState state) throws InvalidMoveException {
        return moveToCity(currentPlayer, toFollow.getCity(), state);
    }

    public void agentDecision(IState state) {
        Player player = state.getPlayerRepository().getCurrentPlayer();
        int possibleActions = 9;
        int random = new Random().nextInt(possibleActions);

        ActionType selectedAction = null;
        RoleAction roleAction = null;
        switch (random) {
            case 0 -> {
                if (DEBUG) System.out.println("move");
                City next = player.getCity().getNeighbors().get(new Random().nextInt(player.getCity().getNeighbors().size() - 1));
                try {
                    state.getPlayerRepository().drive(player, next, state, true);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(DEBUG) System.out.println("Agent moving to "+ next.getName());
            }
            case 1 -> {
                int rnd = new Random().nextInt(state.getCityRepository().getCities().size() - 1);
                ArrayList<City> temp = new ArrayList<>(state.getCityRepository().getCities().values());
                if (DEBUG) System.out.println("charter to " + temp.get(rnd));
                try {
                    state.getPlayerRepository().charter(player, temp.get(rnd), state);
                } catch (Exception e) {
                    if (DEBUG) System.out.println("FAILED WITH ERROR");
                    agentDecision(state);
                }

            }
            case  2 -> {
                if(DEBUG) System.out.println("direct");
                CityCard cityCard = (CityCard) player.getHand().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                if (cityCard!= null){
                    if(DEBUG) System.out.println("direct to " + cityCard.getCity().getName());
                    state.getBoardRepository().setSelectedCity(cityCard.getCity());
                    try{
                        state.getPlayerRepository().direct(player, cityCard.getCity(), state);
                    }catch(Exception e){
                        if(DEBUG) System.out.println("FAILED WITH ERROR");
                        agentDecision(state);
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
            }
            case 3 -> {
                String name = null;
                if (DEBUG) System.out.print("shuttle");
                if (state.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null).count() >= 2 &&
                        player.getCity().getResearchStation() != null) {
                    City city = state.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                    if (city != null) {
                        if (DEBUG) System.out.println(" to " + city.getName());
                        state.getBoardRepository().setSelectedCity(city);
                        try {
                            name = city.getName();
                            state.getPlayerRepository().shuttle(player, city, state);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
            }
            case 4 -> {
                if(DEBUG) System.out.println("researchStation");
                if (player.getCity().getResearchStation() == null && player.getHand().stream().filter(c -> {
                    if (c instanceof CityCard card) return card.getCity().equals(player.getCity());
                    return false;
                }).findFirst().orElse(null) != null) {
                    try{
                        state.getPlayerRepository().buildResearchStation(player, player.getCity(), state);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
            }
            case 5 -> {
                if(DEBUG) System.out.println("treat in " + player.getCity().getName());
                if (!player.getCity().getCubes().isEmpty()) {
                    try {
                        state.getPlayerRepository().treat(player, player.getCity(), player.getCity().getCubes().get(0).getColor(), state);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
            }
            case 6 -> {
                if(DEBUG) System.out.println("share");
                Optional<PlayerCard> sameCityCard = player.getHand().stream().filter(c -> c instanceof CityCard && ((CityCard) c).getCity().equals(player.getCity())).findFirst();
                if(!sameCityCard.isPresent() || player.getCity().getPawns().size() <= 1){
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
                else{
                    player.getHand().remove(sameCityCard.get());
                    int rnd = new Random().nextInt(player.getCity().getPawns().size()-1);
                    player.getCity().getPawns().get(rnd).addHand(sameCityCard.get());
                    state.getPlayerRepository().getLog().addStep(" shared card: " + sameCityCard.get().getName(), player.getCity(), player);
                }
            }
            case 7 -> {
                var sameColorCard = player.getHand().stream().filter(c -> c instanceof CityCard).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                if(DEBUG) System.out.println("discoverCure");
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
                    if (DEBUG) System.out.println(color.getName());
                    Color finalColor = color;
                    Optional<Cure> cure = state.getDiseaseRepository().getCures().values().stream().filter(c -> {
                        return c.getColor().equals(finalColor);
                    }).findFirst();
                    if (cure.isPresent()) {
                        try {
                            state.getDiseaseRepository().discoverCure(player, cure.get());
                            state.getPlayerRepository().getLog().addStep(" discovered cure", player.getCity(), player);
                        } catch (UnableToDiscoverCureException | GameWonException e) {
                            if (DEBUG) System.out.println("FAILED");
                            agentDecision(state);
                        }
                    }
                    else {
                        if(DEBUG) System.out.println("FAILED");
                        agentDecision(state);
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                }
            }
            case 8 -> {
                if(DEBUG) System.out.println("roleaction");
                List<RoleAction> l = player.getRole().actions();
                if(l.size() == 0){
                    if(DEBUG) System.out.println("FAILED");
                    agentDecision(state);
                } else {
                    int rnd = new Random().nextInt(l.size());
                    roleAction(l.get(rnd), player, state);
                }
            }
            default -> {
                selectedAction = ActionType.SKIP_ACTION;
                if (DEBUG) System.out.println("Skip");
            }
        }
        state.getBoardRepository().setSelectedPlayerAction(selectedAction == null ? ActionType.SKIP_ACTION : selectedAction);
        state.getBoardRepository().setSelectedRoleAction(roleAction == null ? RoleAction.NO_ACTION : roleAction);

    }

    public void roleAction(RoleAction roleAction, Player player, IState state) {
        try {
            if (roleAction.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                LinkedList<PlayerCard> ecards = state.getCardRepository().getEventDiscardPile();
                if (ecards.size() > 0) {
                    int random = new Random().nextInt(ecards.size() - 1);
                    player.addHand(ecards.get(random));
                    ecards.remove(random);
                    state.getPlayerRepository().getLog().addStep(" uses Roleaction: Take discarded event card", player.getCity(), player);
                } else {
                    if(DEBUG) System.out.println("Take event card - FAILED");
                    agentDecision(state);
                }
            } else if (roleAction.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)) {
                if(player.getCity().getResearchStation() != null){
                    int random = new Random().nextInt(state.getCityRepository().getCities().values().size() - 1);
                    ArrayList<City> cities = new ArrayList<>();
                    state.getCityRepository().getCities().values().forEach(c -> {
                        cities.add(c);
                    });
                    try{
                        state.getPlayerRepository().shuttle(player, cities.get(random), state);
                    }
                    catch (InvalidMoveException e) {
                        if(DEBUG) System.out.println("Move from research station anywhere - FAILED WITH ERROR");
                        agentDecision(state);
                    }
                }
                else{
                    if(DEBUG) System.out.println("Move from research station anywhere - FAILED");
                    agentDecision(state);
                }
            } else if (roleAction.equals(RoleAction.BUILD_RESEARCH_STATION)){
                if(player.getCity().getResearchStation() == null) {
                    state.getPlayerRepository().buildResearchStation(player, player.getCity(), state);
                    state.getPlayerRepository().getLog().addStep(" uses Roleaction: Build research station", player.getCity(), player);
                }
                else{
                    if(DEBUG) System.out.println("Build research station - FAILED");
                    agentDecision(state);
                }
            } else if (roleAction.equals(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN)) {
                //TODO add dispatcher stuff once event cards are merged in
                state.getPlayerRepository().getLog().addStep(" uses Roleaction: Dispatcher", player.getCity(), player);
            } else{
                if(DEBUG) System.out.println("Roleaction - FAILED");
                agentDecision(state);
            }
        } catch (ResearchStationLimitException | InvalidMoveException | CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

