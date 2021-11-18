package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.InvalidMoveException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;

public class PlayerRepository {
    private static int ACTION_COUNT = 4;
    private static int DRAW_COUNT = 2;
    private static int INFECTION_COUNT = 2;

    private HashMap<String, Player> players;
    private Stack<Role> availableRoles;
    private Player currentPlayer;
    private Queue<Player> playerOrder;
    private RoundState currentRoundState = null;

    private int actionsLeft = ACTION_COUNT;
    private int drawLeft = DRAW_COUNT;
    private int infectionLeft = INFECTION_COUNT;

    public void reset() {
        this.players = new HashMap<>();
        this.currentPlayer = null;
        this.currentRoundState = null;

        this.actionsLeft = ACTION_COUNT;
        this.drawLeft = DRAW_COUNT;
        this.infectionLeft = INFECTION_COUNT;

        this.availableRoles = new Stack<>();
        availableRoles.add(new ContingencyPlanner());
        availableRoles.add(new Dispatcher());
        availableRoles.add(new Medic());
        availableRoles.add(new OperationsExpert());
        availableRoles.add(new QuarantineSpecialist());
        availableRoles.add(new Researcher());
        availableRoles.add(new Scientist());

        Collections.shuffle(availableRoles);
    }

    public void drive(Player player, City city, boolean careAboutNeighbors) throws InvalidMoveException {
        if (player.getCity().equals(city) || ((!player.getCity().getNeighbors().contains(city) && careAboutNeighbors))) {
            throw new InvalidMoveException(city, player);
        }

        city.addPawn(player);

        if (player.getRole().events(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE)) {
            city.getCubes().forEach(c -> {
                Cure found = FactoryProvider.getDiseaseRepository().getCures().get(c.getColor());

                if (found != null) {
                    if (found.isDiscovered()) {
                        city.getCubes().removeIf(cb -> cb.getColor().equals(found.getColor()));
                    }
                }
            });
        }
    }

    public void drive(Player player, City city) throws InvalidMoveException {
        drive(player, city, true);
    }

    public void direct(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city);
            return;
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city);
            return;
        }

        PlayerCard pc = player.getHand().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(city);
            }

            return false;
        }).findFirst().orElse(null);

        // If player doesn't have the city card, it can't make this move.
        if (pc == null) {
            throw new InvalidMoveException(city, player);
        }

        player.getHand().remove(pc);

        drive(player, city, false);
    }

    public void charter(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city);
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city);
        }

        PlayerCard pc = player.getHand().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(city);
            }
            return false;
        }).findFirst().orElse(null);

        // If player doesn't have city card of his current city, it can't make this
        // move.
        if (pc == null) {
            throw new InvalidMoveException(city, player);
        }

        player.getHand().remove(pc);
        drive(player, city, false);
    }

    public void shuttle(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        if (player.getCity().getResearchStation() == null) {
            throw new InvalidMoveException(city, player);
        } else if (city.getResearchStation() == null) {
            if (!player.getRole().getName().equals("Operations Expert"))
                throw new InvalidMoveException(city, player);
            else {
                FactoryProvider.getBoardRepository().getUsedActions().add(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY);
            }

        }

        drive(player, city, false);
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, Player> players) {
        this.players = players;
    }

    public Stack<Role> getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(Stack<Role> availableRoles) {
        this.availableRoles = availableRoles;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Queue<Player> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(Queue<Player> playerOrder) {
        this.playerOrder = playerOrder;
    }

    public RoundState getCurrentRoundState() {
        return currentRoundState;
    }

    public void setCurrentRoundState(RoundState currentRoundState) {
        this.currentRoundState = currentRoundState;
    }

    public int getActionsLeft() {
        return actionsLeft;
    }

    public void setActionsLeft(int actionsLeft) {
        this.actionsLeft = actionsLeft;
    }

    public int getDrawLeft() {
        return drawLeft;
    }

    public void setDrawLeft(int drawLeft) {
        this.drawLeft = drawLeft;
    }

    public int getInfectionLeft() {
        return infectionLeft;
    }

    public void setInfectionLeft(int infectionLeft) {
        this.infectionLeft = infectionLeft;
    }
}
