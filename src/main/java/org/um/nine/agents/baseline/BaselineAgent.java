package org.um.nine.agents.baseline;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.MedicRole;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.*;

import java.util.Random;
import java.util.stream.Collectors;



public class BaselineAgent {

    @Inject
    private IPlayerRepository playerRepository;
    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private ICityRepository cityRepository;



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
        System.out.println(random);

            switch (random) {
                case 0 -> {
                    City next = player.getCity().getNeighbors().get(0);
                    try{
                        playerRepository.drive(player, next, true);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("Agent moving to "+ next.getName());
                }
                case 1 -> {
                    PlayerCard toMove = player.getHandCards().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                    if (toMove != null) {
                        City next = ((CityCard) toMove).getCity();
                        try{
                            playerRepository.charter(player, ((CityCard) toMove).getCity());
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    System.out.println("charter");
                }
                case  2 -> {
                    CityCard cityCard = (CityCard) player.getHandCards().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                    if (cityCard!= null){
                        boardRepository.setSelectedCity(cityCard.getCity());
                        try{
                            playerRepository.direct(player, cityCard.getCity());
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    System.out.println("direct");
                }
                case 3 -> {
                    if (cityRepository.getCities().values().stream().filter(c -> c.getResearchStation()!= null).count() >=2 &&
                            player.getCity().getResearchStation() != null){
                        City city = cityRepository.getCities().values().stream().filter( c-> c.getResearchStation() != null && !c.equals(player.getCity())).findFirst().orElse(null);
                        if (city!= null){
                            boardRepository.setSelectedCity(city);
                            try{
                                playerRepository.shuttle(player, city);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("shuttle");
                }
                case 4 -> {
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
                    System.out.println("researchStation");
                }
                case 5 -> {
                    if (!player.getCity().getCubes().isEmpty()) {
                        //TODO implement disease treating for bots
                        boardRepository.setSelectedCity(player.getCity());
                        selectedAction = ActionType.TREAT_DISEASE;
                    }
                    System.out.println("treat");
                }
                case 6 -> {
                    //TODO : implement share knowledge automatic approval
                    System.out.println("share");
                }
                case 7 -> {
                    var sameColorCard = player.getHandCards().stream().filter(c -> c instanceof CityCard).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                    if (sameColorCard.values().size()>=5 || (player.getRole() instanceof MedicRole) && sameColorCard.values().size() >=4) {
                        sameColorCard.entrySet().forEach(System.out::println);
                        selectedAction = ActionType.DISCOVER_CURE;
                    }
                    System.out.println("discoverCure");
                }
                case 8 -> {
                    for (RoleAction r : RoleAction.values()){
                        if (player.getRole().actions(r))
                            roleAction = r;
                    }
                    System.out.println("roleaction");
                }
                default -> {
                    selectedAction = ActionType.SKIP_ACTION;
                    System.out.println("Skip");
                }
            }
        boardRepository.setSelectedPlayerAction(selectedAction == null? ActionType.SKIP_ACTION : selectedAction);
        boardRepository.setSelectedRoleAction(roleAction == null ? RoleAction.NO_ACTION : roleAction);

    }
}

