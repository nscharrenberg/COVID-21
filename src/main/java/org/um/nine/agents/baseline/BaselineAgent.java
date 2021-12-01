package org.um.nine.agents.baseline;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.apache.tools.ant.taskdefs.modules.Link;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.City;
import org.um.nine.domain.Cure;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.MedicRole;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class BaselineAgent {

    @Inject
    private IPlayerRepository playerRepository;
    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private ICardRepository cardRepository;

    private boolean DEBUG = true;

    public boolean moveToCity(Player currentPlayer, City target) throws InvalidMoveException {
        if (currentPlayer.getCity().getName().equals(target.getName())) return true;

        if (currentPlayer.getCity().getNeighbors().contains(target)) {
            playerRepository.drive(currentPlayer, target, true);
            return true;
        }

        PlayerCard pc = currentPlayer.getHandCards().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(target);
            }

            return false;
        }).findFirst().orElse(null);

        if (pc != null) {
            currentPlayer.getHandCards().remove(pc);
            playerRepository.drive(currentPlayer, target, false);
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
                        playerRepository.drive(player, next, true);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(DEBUG) System.out.println("Agent moving to "+ next.getName());
                }
                case 1 -> {
                    if(DEBUG) System.out.println("charter");
                    int rnd = new Random().nextInt(cityRepository.getCities().size()-1);
                    ArrayList<City> temp = new ArrayList<>();
                    cityRepository.getCities().values().forEach(c -> {
                        temp.add(c);
                    });
                    if(DEBUG) System.out.println("charter to " + temp.get(rnd));
                    try{
                        playerRepository.charter(player, temp.get(rnd));
                    }catch(Exception e){
                        if(DEBUG) System.out.println("FAILED");
                        randomAction(player);
                    }

                }
                case  2 -> {
                    if(DEBUG) System.out.println("direct");
                    CityCard cityCard = (CityCard) player.getHandCards().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                    if (cityCard!= null){
                        if(DEBUG) System.out.println("direct to " + cityCard.getCity().getName());
                        boardRepository.setSelectedCity(cityCard.getCity());
                        try{
                            playerRepository.direct(player, cityCard.getCity());
                        }catch(Exception e){
                            e.printStackTrace();
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
                    if (cityRepository.getCities().values().stream().filter(c -> c.getResearchStation()!= null).count() >=2 &&
                            player.getCity().getResearchStation() != null){
                        City city = cityRepository.getCities().values().stream().filter( c-> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                        if (city!= null){
                            if(DEBUG) System.out.println(" to " + city.getName());
                            boardRepository.setSelectedCity(city);
                            try{
                                name = city.getName();
                                playerRepository.shuttle(player, city);
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
                    if (player.getCity().getResearchStation() == null && player.getHandCards().stream().filter(c -> {
                        if (c instanceof CityCard card) return card.getCity().equals(player.getCity());
                        return false;
                    }).findFirst().orElse(null) != null) {
                        try{
                            cityRepository.addResearchStation(player.getCity(), player);
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
                        try{
                            diseaseRepository.treat(player, player.getCity(), player.getCity().getCubes().get(0));
                        } catch (NoCityCardToTreatDiseaseException e) {
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
                    var sameCityCard = player.getHandCards().stream().filter(c -> c instanceof CityCard && ((CityCard) c).getCity().equals(player.getCity())).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                    if(sameCityCard.isEmpty() || player.getCity().getPawns().size() <= 1){
                        if(DEBUG) System.out.println("FAILED");
                        randomAction(player);
                    }
                    else{
                        player.getHandCards().remove((PlayerCard) sameCityCard);
                        int rnd = new Random().nextInt(player.getCity().getPawns().size()-1);
                        player.getCity().getPawns().get(rnd).addCard((PlayerCard) sameCityCard);
                    }
                }
                case 7 -> {
                    var sameColorCard = player.getHandCards().stream().filter(c -> c instanceof CityCard).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                    if(DEBUG) System.out.println("discoverCure");
                    AtomicInteger red = new AtomicInteger();
                    AtomicInteger black = new AtomicInteger();
                    AtomicInteger blue = new AtomicInteger();
                    AtomicInteger yellow = new AtomicInteger();
                    sameColorCard.forEach((color, playerCards) -> {
                        if (ColorRGBA.Red.equals(color)) {
                            red.getAndIncrement();
                        } else if (ColorRGBA.Black.equals(color)) {
                            black.getAndIncrement();
                        } else if (ColorRGBA.Blue.equals(color)) {
                            blue.getAndIncrement();
                        } else if (ColorRGBA.Yellow.equals(color)) {
                            yellow.getAndIncrement();
                        }
                    });
                    ColorRGBA color = ColorRGBA.Pink;
                    boolean curable = false;
                    if(red.get() >= 5|| (player.getRole() instanceof MedicRole) && red.get() >=4){
                        curable = true;
                        color = ColorRGBA.Red;
                    }else if(black.get() >= 5|| (player.getRole() instanceof MedicRole) && black.get() >=4){
                        curable = true;
                        color = ColorRGBA.Black;
                    }else if(blue.get() >= 5|| (player.getRole() instanceof MedicRole) && blue.get() >=4){
                        curable = true;
                        color = ColorRGBA.Blue;
                    }else if(yellow.get() >= 5|| (player.getRole() instanceof MedicRole) && yellow.get() >=4){
                        curable = true;
                        color = ColorRGBA.Yellow;
                    }

                    if (curable) {
                        if(DEBUG) System.out.println(color);
                        ColorRGBA finalColor = color;
                        Optional<Cure> cure = diseaseRepository.getCures().values().stream().filter(c -> {
                            if(c.getColor().equals(finalColor)){
                                return true;
                            }
                            return false;
                        }).findFirst();
                        if(cure.isPresent()){
                            try {
                                diseaseRepository.discoverCure(player,cure.get());
                            } catch (UnableToDiscoverCureException e) {
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
                        System.out.println("FAILED");
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
        boardRepository.setSelectedPlayerAction(selectedAction == null? ActionType.SKIP_ACTION : selectedAction);
        boardRepository.setSelectedRoleAction(roleAction == null ? RoleAction.NO_ACTION : roleAction);

    }

    public void roleAction(RoleAction roleAction, Player player){
        try{
            if (roleAction.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                LinkedList<PlayerCard> ecards = cardRepository.getEventDiscardPile();
                if(ecards.size() > 0){
                    int random = new Random().nextInt(ecards.size()-1);
                    player.addCard(ecards.get(random));
                    ecards.remove(random);
                }
                else{
                    if(DEBUG) System.out.println("Take event card - FAILED");
                    randomAction(player);
                }
            } else if (roleAction.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)) {
                if(player.getCity().getResearchStation() != null){
                    int random = new Random().nextInt(cityRepository.getCities().values().size()-1);
                    ArrayList<City> cities = new ArrayList<>();
                    cityRepository.getCities().values().forEach(c -> {
                        cities.add(c);
                    });
                    playerRepository.shuttle(player, cities.get(random));
                }
                else{
                    if(DEBUG) System.out.println("Move from research station anywhere - FAILED");
                    randomAction(player);
                }
            } else if (roleAction.equals(RoleAction.BUILD_RESEARCH_STATION)){
                if(player.getCity().getResearchStation() == null){
                    cityRepository.addResearchStation(player.getCity(), player);
                }
                else{
                    if(DEBUG) System.out.println("Build research station - FAILED");
                    randomAction(player);
                }
            }
            //TODO add dispatcher stuff once event cards are merged in

        } catch (ResearchStationLimitException | InvalidMoveException | CityAlreadyHasResearchStationException | NoCityCardToBuildResearchStation e) {
            e.printStackTrace();
        }
    }
}

