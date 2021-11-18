package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.headless.game.exceptions.InvalidMoveException;
import org.um.nine.headless.game.exceptions.NoActionSelectedException;
import org.um.nine.headless.game.exceptions.ResearchStationLimitException;

import java.util.*;

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

    /**
     * Check what the next turn is for the player
     * @param currentState
     * @return
     */
    public RoundState nextTurn(RoundState currentState) {
        if (currentState == null || currentState == RoundState.ACTION) {
            if (currentState == null) {
                this.currentRoundState = RoundState.ACTION;
            }

            actionsLeft--;

            if (actionsLeft <= 0) {
                this.currentRoundState = RoundState.DRAW;
                return this.currentRoundState;
            }

            return this.currentRoundState;
        } else if (currentState == RoundState.DRAW) {
            drawLeft--;

            if (drawLeft <= 0) {
                this.currentRoundState = RoundState.INFECT;
                return this.currentRoundState;
            }

            return this.currentRoundState;
        } else if (currentState == RoundState.INFECT) {
            infectionLeft--;

            if (infectionLeft <= 0) {
                infectionLeft = Objects.requireNonNull(FactoryProvider.getDiseaseRepository().getInfectionRate().stream().filter(org.um.nine.v1.domain.Marker::isCurrent).findFirst().orElse(null)).getCount();

                this.currentRoundState = null;
                nextPlayer();

                return this.currentRoundState;
            }

            return this.currentRoundState;
        }

        throw new IllegalStateException();
    }

    public void treat(Player player, City city, Color color) throws Exception {
        if (player.getCity().equals(city)) {
            throw new Exception("Only able to treat disease in players current city");
        }

        if (city.getCubes().isEmpty()) {
            throw new Exception("There are no disease to be treated in " + city.getName());
        }

        FactoryProvider.getDiseaseRepository().treat(player, city, color);
        nextTurn(this.currentRoundState);
    }

    public void share(Player player, Player target, City city, PlayerCard card, boolean giveCard) throws Exception {
        if (city.getPawns().size() <= 1) {
            throw new Exception("Can not share when you are the only pawn in the city");
        }

        RoleAction action = RoleAction.GIVE_PLAYER_CITY_CARD;

        if (player.getRole().actions(action) && FactoryProvider.getBoardRepository().getSelectedRoleAction().equals(action) && !FactoryProvider.getBoardRepository().getUsedActions().contains(action)) {
            FactoryProvider.getBoardRepository().getUsedActions().add(action);
        } else if (!player.getCity().equals(city)) {
            throw new Exception("Only able to share knowledge on the city the player is currently at.");
        }

        if (giveCard) {
            if (!currentPlayer.getHand().contains(card)) {
                throw new Exception(currentPlayer.getName() + " does not have a card to share");
            }

            currentPlayer.removeHand(card);
            target.addHand(card);
            nextTurn(this.currentRoundState);
            return;
        }

        if (!target.getHand().contains(card)) {
            throw new Exception(target.getName() + " does not have a card to share");
        }

        target.removeHand(card);
        currentPlayer.addHand(card);
        nextTurn(this.currentRoundState);
    }

    public void buildResearchStation(Player player, City city) throws Exception {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((FactoryProvider.getCityRepository().getResearchStations().size()) >= Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        RoleAction action = RoleAction.BUILD_RESEARCH_STATION;

        if (player.getRole().actions(action) && FactoryProvider.getBoardRepository().getSelectedRoleAction().equals(action) && !FactoryProvider.getBoardRepository().getUsedActions().contains(action)) {
            FactoryProvider.getBoardRepository().getUsedActions().add(action);
        } else {
            PlayerCard pc = player.getHand().stream().filter(c -> {
                if (c instanceof CityCard cc) {
                    return cc.getCity().equals(player.getCity());
                }

                return false;
            }).findFirst().orElse(null);

            // If player doesn't have city card of his current city, it can't make this move.
            if (pc == null) {
                throw new InvalidMoveException(city, player);
            }

            player.getHand().remove(pc);
        }

        FactoryProvider.getCityRepository().addResearchStation(city);
    }

    public void playerAction(ActionType type) throws NoActionSelectedException {
        if (currentRoundState == null) {
            nextTurn(null);
        }

        if (currentRoundState.equals(RoundState.ACTION)) {
            if (type == null && FactoryProvider.getBoardRepository().getSelectedRoleAction() == null) {
                throw new NoActionSelectedException();
            }

            City city = FactoryProvider.getBoardRepository().getSelectedCity();
            Player player = this.currentPlayer;

            if (type == null) {
                type = ActionType.NO_ACTION;
                return;
            }

            BoardRepository boardRepository = FactoryProvider.getBoardRepository();

            if (boardRepository.getSelectedRoleAction().equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {

            } else if (boardRepository.getSelectedRoleAction()
                        .equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY) || type.equals(ActionType.SHUTTLE)) {

            } else if (boardRepository.getSelectedRoleAction().equals(RoleAction.BUILD_RESEARCH_STATION)
                        || type.equals(ActionType.BUILD_RESEARCH_STATION)) {

            } else if (type.equals(ActionType.DRIVE)) {

            } else if (type.equals(ActionType.DIRECT_FLIGHT)) {

            } else if (type.equals(ActionType.CHARTER_FLIGHT)) {

            } else if (type.equals(ActionType.TREAT_DISEASE)) {

            } else if (type.equals(ActionType.SHARE_KNOWLEDGE)) {

            } else if (type.equals(ActionType.DISCOVER_CURE)) {

            } else if (type.equals(ActionType.SKIP_ACTION)) {

            }




        } else if (currentRoundState.equals(RoundState.DRAW)) {
            FactoryProvider.getCardRepository().drawPlayerCard();

            nextTurn(currentRoundState);

            if (drawLeft >= 0) {
                playerAction(null);
            }
        } else if (currentRoundState.equals(RoundState.INFECT)) {
            FactoryProvider.getCardRepository().drawInfectionCard();

            nextTurn(currentRoundState);

            if (infectionLeft >= 0) {
                FactoryProvider.getBoardRepository().setSelectedRoleAction(null);
                playerAction(null);
            }
        }
    }

    public void nextPlayer() {
        Player nextPlayer = this.playerOrder.poll();

        this.playerOrder.add(nextPlayer);
        setCurrentPlayer(this.playerOrder.peek());

        resetRound();
    }

    public void resetRound() {
        this.drawLeft = DRAW_COUNT;
        this.infectionLeft = INFECTION_COUNT;
        this.actionsLeft = ACTION_COUNT;
    }

    public void decidePlayerOrder() {
        this.playerOrder = new LinkedList<>();

        HashMap<String, Integer> highestPopulation = new HashMap<>();

        this.players.forEach((key, player) -> {
            int highestPopulationCount = 0;

            for (PlayerCard c : player.getHand()) {
                if (c instanceof CityCard tempCityCard) {
                    highestPopulationCount = Math.max(tempCityCard.getCity().getPopulation(), highestPopulationCount);
                }
            }

            highestPopulation.put(key, highestPopulationCount);
        });

        highestPopulation.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(p -> {
            this.playerOrder.add(this.players.get(p.getKey()));
        });
    }

    public void assignRoleToPlayer(Player player) {
        Role role = availableRoles.pop();

        player.setRole(role);
    }

    public void roleAction(RoleAction roleAction, Player player) {
        if (roleAction.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {

        } else if (roleAction.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)) {

        } else if (roleAction.equals(RoleAction.BUILD_RESEARCH_STATION)) {

        }
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
