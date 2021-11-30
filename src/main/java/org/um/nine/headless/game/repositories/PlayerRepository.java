package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.agents.Log;
import org.um.nine.headless.game.contracts.repositories.IBoardRepository;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;

public class PlayerRepository implements IPlayerRepository {
    private static int ACTION_COUNT = 4;
    private static int DRAW_COUNT = 2;
    private static int INFECTION_COUNT = 2;

    private HashMap<String, Player> players = new HashMap<>();
    private Stack<Role> availableRoles;
    private Player currentPlayer;
    private Queue<Player> playerOrder;
    private RoundState currentRoundState = null;

    private int actionsLeft = ACTION_COUNT;
    private int drawLeft = DRAW_COUNT;
    private int infectionLeft = INFECTION_COUNT;

    private Log log = new Log();
    private boolean logged = false;

    public PlayerRepository() {
        reset();
    }

    /**
     * Resets state to its original data
     */
    @Override
    public void reset() {
        this.log = new Log();

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

    /**
     * Move a player to an adjacent city of its current city.
     * Note 1: Only moves over adjacent edges when "careAboutNeighbors" is true, and freely move around when its false.
     * Note 2: When player has AUTO_REMOVE_CUBES_OF_CURED_DISEASE permission, it'll remove all cubes when the player reaches that city and a cure has been found.
     * @param player - the player to move
     * @param city - the city the player wants to move to
     * @param careAboutNeighbors - whether or not we can freely move or have to keep edges into account
     * @throws InvalidMoveException - thrown when the move is invalid
     */
    @Override
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

        if(!logged){
            log.addStep(" drive to " + city.getName(), city);
        }
        else{
            logged = false;
        }
    }

    @Override
    public void drive(Player player, City city) throws InvalidMoveException {
        drive(player, city, true);
    }

    /**
     * Discard a city card to move to the city named on the card
     * Note 1: We do not direct flight when we are are on a research station.
     * Note 2: We do not direct flight when we are moving to an adjacent city
     * Note 3: We do not direct flight when the player does not have the city card in its hand
     * @param player - the player to move
     * @param city - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
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

        log.addStep(" direct to " + city.getName(), city);
        logged = true;
        drive(player, city, false);
    }

    /**
     * Discard the city card that matches the city the player is in to move to any city
     * Note 1: We do not charter flight when we are are on a research station.
     * Note 2: We do not charter flight when we are moving to an adjacent city
     * Note 3: We do not charter flight when the player does not have the city card in its hand
     * @param player - the player to move
     * @param city - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
    public void charter(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city);
            return; //todo needs to be fixed in main code
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city);
            return; //todo needs to be fixed in main code
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

        log.addStep(" charter to " + city.getName(), city);
        logged = true;
        drive(player, city, false);
    }

    /**
     * Move from a city with a research station to any other city that has a research station.
     * Note 1: Invalid move when you are trying to to shuttle from or to a city without a research station
     * Note 2: Operations expert can move to any city from a research station
     * @param player - the player to move
     * @param city - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
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

        log.addStep(" shuttle to " + city.getName(), city);
        logged = true;
        drive(player, city, false);
    }

    @Override
    public RoundState nextTurn() {
        return nextTurn(this.currentRoundState);
    }

    /**
     * Check what the next turn is for the player
     * @param currentState
     * @return
     */
    @Override
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
                infectionLeft = Objects.requireNonNull(FactoryProvider.getDiseaseRepository().getInfectionRates().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();

                this.currentRoundState = null;
                nextPlayer();

                return this.currentRoundState;
            }

            return this.currentRoundState;
        }

        throw new IllegalStateException();
    }

    /**
     * Try to treat a disease
     * @param player - the player that wants to treat a disease
     * @param city - the city to treat the disease in
     * @param color - The color of the disease to treat
     * @throws Exception - Thrown when a disease couldn't be treated
     */
    @Override
    public void treat(Player player, City city, Color color) throws Exception {
        if (!player.getCity().equals(city)) {
            throw new Exception("Only able to treat disease in players current city");
        }

        if (city.getCubes().isEmpty()) {
            throw new Exception("There are no disease to be treated in " + city.getName());
        }

        FactoryProvider.getDiseaseRepository().treat(player, city, color);
        log.addStep(" treat in " + city.getName(), city);
        nextTurn(this.currentRoundState);
    }

    @Override
    public void share(Player player, Player target, City city, PlayerCard card) throws Exception {
        if (city.getPawns().size() <= 1) {
            throw new Exception("Can not share when you are the only pawn in the city");
        }

        RoleAction action = RoleAction.GIVE_PLAYER_CITY_CARD;

        if (player.getRole().actions(action) && FactoryProvider.getBoardRepository().getSelectedRoleAction().equals(action) && !FactoryProvider.getBoardRepository().getUsedActions().contains(action)) {
            FactoryProvider.getBoardRepository().getUsedActions().add(action);
        } else if (!player.getCity().equals(city)) {
            throw new Exception("Only able to share knowledge on the city the player is currently at.");
        }

        if (player.getHand().contains(card)) {
            player.discard(card);
            target.addHand(card);
            nextTurn(this.currentRoundState);
            return;
        }

        if (!target.getHand().contains(card)) {
            throw new UnableToShareKnowledgeException(city, player, target);
        }

        target.discard(card);
        player.addHand(card);

        log.addStep(" shared " + city.getName() + " with " + target.getName(), city);
        nextTurn(this.currentRoundState);
    }

    @Override
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

        log.addStep(" build research station in " + city.getName(), city);
        FactoryProvider.getCityRepository().addResearchStation(city);
    }

    @Override
    public void playerAction(ActionType type, Objects... args) throws Exception {
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

            IBoardRepository boardRepository = FactoryProvider.getBoardRepository();

            if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction().equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                if (args.length <= 0) {
                    throw new Exception("You need to select an event Card from the discard pile.");
                }

                Object found = args[0];

                try {
                    PlayerCard card = (PlayerCard) found;

                    if (!FactoryProvider.getCardRepository().getEventDiscardPile().contains(card)) {
                        throw new Exception("You need to select an event Card from the discard pile.");
                    }

                    FactoryProvider.getCardRepository().getEventDiscardPile().remove(card);
                    player.addHand(card);
                    nextTurn();
                } catch (Exception e) {
                    throw new Exception("You need to select an event Card from the discard pile.");
                }
            } else if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction()
                        .equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY) || type.equals(ActionType.SHUTTLE)) {
                shuttle(player, city);
                FactoryProvider.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
            } else if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction().equals(RoleAction.BUILD_RESEARCH_STATION)
                        || type.equals(ActionType.BUILD_RESEARCH_STATION)) {
                buildResearchStation(player, city);
                FactoryProvider.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
            } else if (type.equals(ActionType.DRIVE)) {
                drive(player, city);
            } else if (type.equals(ActionType.DIRECT_FLIGHT)) {
                direct(player, city);
            } else if (type.equals(ActionType.CHARTER_FLIGHT)) {
                charter(player, city);
            } else if (type.equals(ActionType.TREAT_DISEASE)) {
                if (args.length <= 0) {
                    throw new Exception("You need to provide the disease to treat.");
                }

                Object found = args[0];

                try {
                    Color color = (Color) found;
                    treat(player, city, color);
                    nextTurn();
                } catch (Exception e) {
                    throw new Exception("You need to provide the disease to treat.");
                }

            } else if (type.equals(ActionType.SHARE_KNOWLEDGE)
                    || boardRepository.getSelectedRoleAction() != null && FactoryProvider.getBoardRepository().getSelectedRoleAction().equals(RoleAction.GIVE_PLAYER_CITY_CARD)) {
                if (args.length <= 0) {
                    throw new Exception("You need to provide the player to negotiate with, the card you want, and whether you are giving that card.");
                }

                Object targetObj = args[0];
                Object cardObj = args[1];

                try {
                    Player target = (Player) targetObj;
                    PlayerCard card = (PlayerCard) cardObj;
                    share(player, target, city, card);
                    nextTurn();
                    FactoryProvider.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
                } catch (Exception e) {
                    throw new Exception("You need to provide the player to negotiate with, the card you want, and whether you are giving that card.");
                }

            } else if (type.equals(ActionType.DISCOVER_CURE)) {
                if (args.length <= 0) {
                    throw new Exception("You need to select a cure to discover.");
                }

                Object found = args[0];

                try {
                    Cure cure = (Cure) found;
                    FactoryProvider.getDiseaseRepository().discoverCure(player, cure);
                    nextTurn();
                } catch (Exception e) {
                    throw new Exception("You need to select a cure to discover");
                }
            } else if (type.equals(ActionType.SKIP_ACTION)) {
                // TODO: Skip all remaining turns
                nextTurn();
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

    public void createPlayer(String name, boolean isBot) throws PlayerLimitException {
        if (this.players.size() + 1 > Info.PLAYER_THRESHOLD) {
            throw new PlayerLimitException();
        }

        Player player = new Player(name, isBot);
        this.players.put(name, player);
    }

    @Override
    public void nextPlayer() {
        Player nextPlayer = this.playerOrder.poll();

        this.playerOrder.add(nextPlayer);
        setCurrentPlayer(this.playerOrder.peek());

        resetRound();
    }

    @Override
    public void resetRound() {
        this.drawLeft = DRAW_COUNT;
        this.infectionLeft = INFECTION_COUNT;
        this.actionsLeft = ACTION_COUNT;
    }

    @Override
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

    @Override
    public void assignRoleToPlayer(Player player) {
        Role role = availableRoles.pop();

        player.setRole(role);
    }

    @Override
    public void roleAction(RoleAction roleAction, Player player) throws Exception {
        if (roleAction.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {

        } else if (roleAction.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)) {

        } else if (roleAction.equals(RoleAction.BUILD_RESEARCH_STATION)) {

        }
    }

    @Override
    public HashMap<String, Player> getPlayers() {
        return players;
    }

    @Override
    public void setPlayers(HashMap<String, Player> players) {
        this.players = players;
    }

    @Override
    public Stack<Role> getAvailableRoles() {
        return availableRoles;
    }

    @Override
    public void setAvailableRoles(Stack<Role> availableRoles) {
        this.availableRoles = availableRoles;
    }

    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public Queue<Player> getPlayerOrder() {
        return playerOrder;
    }

    @Override
    public void setPlayerOrder(Queue<Player> playerOrder) {
        this.playerOrder = playerOrder;
    }

    @Override
    public RoundState getCurrentRoundState() {
        return currentRoundState;
    }

    @Override
    public void setCurrentRoundState(RoundState currentRoundState) {
        this.currentRoundState = currentRoundState;
    }

    @Override
    public int getActionsLeft() {
        return actionsLeft;
    }

    @Override
    public void setActionsLeft(int actionsLeft) {
        this.actionsLeft = actionsLeft;
    }

    @Override
    public int getDrawLeft() {
        return drawLeft;
    }

    @Override
    public void setDrawLeft(int drawLeft) {
        this.drawLeft = drawLeft;
    }

    @Override
    public int getInfectionLeft() {
        return infectionLeft;
    }

    @Override
    public void setInfectionLeft(int infectionLeft) {
        this.infectionLeft = infectionLeft;
    }

    @Override
    public Log getLog(){
        return log;
    }
}
