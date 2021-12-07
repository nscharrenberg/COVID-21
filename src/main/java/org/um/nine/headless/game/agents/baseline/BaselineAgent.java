package org.um.nine.headless.game.agents.baseline;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class BaselineAgent {

    private boolean DEBUG = false;

    public BaselineAgent() {
    }

    public boolean moveToCity(Player currentPlayer, City target) throws InvalidMoveException {
        if (currentPlayer.getCity().getName().equals(target.getName())) return true;

        if (currentPlayer.getCity().getNeighbors().contains(target)) {
            FactoryProvider.getPlayerRepository().drive(currentPlayer, target, true);
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
            FactoryProvider.getPlayerRepository().drive(currentPlayer, target, false);
            return true;
        }


        /*
        TODO: else find an algorithm to calc shortest/lightest path to get to the target (also in many rounds)
        */

        return false;
    }

    public boolean followPlayer(Player currentPlayer, Player toFollow) throws InvalidMoveException {
        return moveToCity(currentPlayer, toFollow.getCity());
    }


    public void randomAction(Player player) {
        int possibleActions = 9;
        int random = new Random().nextInt(possibleActions);

        ActionType selectedAction = null;
        RoleAction roleAction = null;
        switch (random) {
            case 0 -> {
                if(DEBUG) System.out.println("move");
                City next = player.getCity().getNeighbors().get(new Random().nextInt(player.getCity().getNeighbors().size()-1));
                try{
                    FactoryProvider.getPlayerRepository().drive(player, next, true);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(DEBUG) System.out.println("Agent moving to "+ next.getName());
            }
            case 1 -> {
                int rnd = new Random().nextInt(FactoryProvider.getCityRepository().getCities().size()-1);
                ArrayList<City> temp = new ArrayList<>(FactoryProvider.getCityRepository().getCities().values());
                if(DEBUG) System.out.println("charter to " + temp.get(rnd));
                try{
                    FactoryProvider.getPlayerRepository().charter(player, temp.get(rnd));
                }catch(Exception e){
                    if(DEBUG) System.out.println("FAILED WITH ERROR");
                    randomAction(player);
                }

            }
            case  2 -> {
                if(DEBUG) System.out.println("direct");
                CityCard cityCard = (CityCard) player.getHand().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                if (cityCard!= null){
                    if(DEBUG) System.out.println("direct to " + cityCard.getCity().getName());
                    FactoryProvider.getBoardRepository().setSelectedCity(cityCard.getCity());
                    try{
                        FactoryProvider.getPlayerRepository().direct(player, cityCard.getCity());
                    }catch(Exception e){
                        if(DEBUG) System.out.println("FAILED WITH ERROR");
                        randomAction(player);
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
            }
            case 3 -> {
                String name = null;
                if(DEBUG) System.out.print("shuttle");
                if (FactoryProvider.getCityRepository().getCities().values().stream().filter(c -> c.getResearchStation()!= null).count() >=2 &&
                        player.getCity().getResearchStation() != null){
                    City city = FactoryProvider.getCityRepository().getCities().values().stream().filter( c-> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                    if (city!= null){
                        if(DEBUG) System.out.println(" to " + city.getName());
                        FactoryProvider.getBoardRepository().setSelectedCity(city);
                        try{
                            name = city.getName();
                            FactoryProvider.getPlayerRepository().shuttle(player, city);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
            }
            case 4 -> {
                if(DEBUG) System.out.println("researchStation");
                if (player.getCity().getResearchStation() == null && player.getHand().stream().filter(c -> {
                    if (c instanceof CityCard card) return card.getCity().equals(player.getCity());
                    return false;
                }).findFirst().orElse(null) != null) {
                    try{
                        FactoryProvider.getPlayerRepository().buildResearchStation(player, player.getCity());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
            }
            case 5 -> {
                if(DEBUG) System.out.println("treat in " + player.getCity().getName());
                if (!player.getCity().getCubes().isEmpty()) {
                    try {
                        FactoryProvider.getPlayerRepository().treat(player, player.getCity(), player.getCity().getCubes().get(0).getColor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
            }
            case 6 -> {
                if(DEBUG) System.out.println("share");
                Optional<PlayerCard> sameCityCard = player.getHand().stream().filter(c -> c instanceof CityCard && ((CityCard) c).getCity().equals(player.getCity())).findFirst();
                if(!sameCityCard.isPresent() || player.getCity().getPawns().size() <= 1){
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
                else{
                    player.getHand().remove(sameCityCard.get());
                    int rnd = new Random().nextInt(player.getCity().getPawns().size()-1);
                    player.getCity().getPawns().get(rnd).addHand(sameCityCard.get());
                    FactoryProvider.getPlayerRepository().getLog().addStep(" shared card: " + sameCityCard.get().getName(), player.getCity());
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
                    if(DEBUG) System.out.println(color.getName());
                    Color finalColor = color;
                    Optional<Cure> cure = FactoryProvider.getDiseaseRepository().getCures().values().stream().filter(c -> {
                        if(c.getColor().equals(finalColor)){
                            return true;
                        }
                        return false;
                    }).findFirst();
                    if(cure.isPresent()){
                        try {
                            FactoryProvider.getDiseaseRepository().discoverCure(player,cure.get());
                            FactoryProvider.getPlayerRepository().getLog().addStep(" discovered cure", player.getCity());
                        } catch (UnableToDiscoverCureException | GameWonException e) {
                            if(DEBUG) System.out.println("FAILED");
                            randomAction(player);
                        }
                    }
                    else {
                        if(DEBUG) System.out.println("FAILED");
                        randomAction(player);
                    }
                }
                else {
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
            }
            case 8 -> {
                if(DEBUG) System.out.println("roleaction");
                List<RoleAction> l = player.getRole().actions();
                if(l.size() == 0){
                    if(DEBUG) System.out.println("FAILED");
                    randomAction(player);
                }
                else{
                    int rnd = new Random().nextInt(l.size());
                    roleAction(l.get(rnd),player);
                }
            }
            default -> {
                selectedAction = ActionType.SKIP_ACTION;
                if(DEBUG) System.out.println("Skip");
            }
        }
        FactoryProvider.getBoardRepository().setSelectedPlayerAction(selectedAction == null? ActionType.SKIP_ACTION : selectedAction);
        FactoryProvider.getBoardRepository().setSelectedRoleAction(roleAction == null ? RoleAction.NO_ACTION : roleAction);

    }

    public void roleAction(RoleAction roleAction, Player player){
        try{
            if (roleAction.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                LinkedList<PlayerCard> ecards = FactoryProvider.getCardRepository().getEventDiscardPile();
                if(ecards.size() > 0){
                    int random = new Random().nextInt(ecards.size()-1);
                    player.addHand(ecards.get(random));
                    ecards.remove(random);
                    FactoryProvider.getPlayerRepository().getLog().addStep(" uses Roleaction: Take discarded event card", player.getCity());
                }
                else{
                    if(DEBUG) System.out.println("Take event card - FAILED");
                    randomAction(player);
                }
            } else if (roleAction.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)) {
                if(player.getCity().getResearchStation() != null){
                    int random = new Random().nextInt(FactoryProvider.getCityRepository().getCities().values().size()-1);
                    ArrayList<City> cities = new ArrayList<>();
                    FactoryProvider.getCityRepository().getCities().values().forEach(c -> {
                        cities.add(c);
                    });
                    try{
                        FactoryProvider.getPlayerRepository().shuttle(player, cities.get(random));
                    }
                    catch (InvalidMoveException e) {
                        if(DEBUG) System.out.println("Move from research station anywhere - FAILED WITH ERROR");
                        randomAction(player);
                    }
                }
                else{
                    if(DEBUG) System.out.println("Move from research station anywhere - FAILED");
                    randomAction(player);
                }
            } else if (roleAction.equals(RoleAction.BUILD_RESEARCH_STATION)){
                if(player.getCity().getResearchStation() == null){
                    FactoryProvider.getPlayerRepository().buildResearchStation(player, player.getCity());
                    FactoryProvider.getPlayerRepository().getLog().addStep(" uses Roleaction: Build research station", player.getCity());
                }
                else{
                    if(DEBUG) System.out.println("Build research station - FAILED");
                    randomAction(player);
                }
            } else if (roleAction.equals(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN)) {
                //TODO add dispatcher stuff once event cards are merged in
                FactoryProvider.getPlayerRepository().getLog().addStep(" uses Roleaction: Dispatcher", player.getCity());
            } else{
                if(DEBUG) System.out.println("Roleaction - FAILED");
                randomAction(player);
            }
        } catch (ResearchStationLimitException | InvalidMoveException | CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

