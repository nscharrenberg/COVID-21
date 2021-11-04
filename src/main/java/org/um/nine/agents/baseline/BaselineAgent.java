package org.um.nine.agents.baseline;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.MedicRole;
import org.um.nine.exceptions.InvalidMoveException;

import java.util.Random;
import java.util.stream.Collectors;



public class BaselineAgent {



    @Inject
    private IPlayerRepository playerRepository;




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
        int possibleActions = 6;
        int random = new Random().nextInt(possibleActions);

        try {

            switch (random) {
                case 0 -> {
                    City next = player.getCity().getNeighbors().get(0);
                    playerRepository.drive(player, next);
                    System.out.println("Agent moved to "+ next.getName());
                }
                case 1 -> {
                    PlayerCard toMove = player.getHandCards().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null);
                    if (toMove != null) {
                        City next = ((CityCard) toMove).getCity();
                        playerRepository.shuttle(player, next);
                        System.out.println("Agent shuttled to "+ next.getName());
                    }
                }
                case 2 -> {
                    if (!player.getCity().getCubes().isEmpty()) {
                        playerRepository.treat(player, player.getCity());
                        System.out.println("Agent cured 1 disease cube");
                    }
                }
                case 3 -> {
                    //TODO : USE ROLE ACTION IMPLEMENTATION
                }
                case 4 -> {
                    var sameColorCard = player.getHandCards().stream().filter(c -> c instanceof CityCard).collect(Collectors.groupingBy(c -> ((CityCard)c).getCity().getColor()));
                    if (sameColorCard.values().size()>=5 || (player.getRole() instanceof MedicRole) && sameColorCard.values().size() >=4) {
                        //TODO: add discover cure of the color
                        sameColorCard.entrySet().forEach(System.out::println);
                    }
                }
                case 5 -> {
                    //playerRepository.share(player,player.getCity());
                }
                default -> { }
            }
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }


    }
}

