package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.contracts.repositories.IBoardRepository;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;

import static org.um.nine.headless.game.Settings.*;

public class PlayerRepository implements IPlayerRepository {
    private static final int ACTION_COUNT = 4;
    private static final int DRAW_COUNT = 2;
    private static final int INFECTION_COUNT = 2;
    private static final int mctsIterations = 3;

    private HashMap<String, Player> players = new HashMap<>();
    private Stack<Role> availableRoles;
    private Player currentPlayer;
    private Queue<Player> playerOrder;
    private RoundState currentRoundState = null;

    private int actionsLeft = ACTION_COUNT;
    private int drawLeft = DRAW_COUNT;
    private int infectionLeft = INFECTION_COUNT;
    private Logger log = new Logger();
    private boolean logged = false;

    public PlayerRepository() {

    }

    /**
     * Resets state to its original data
     */
    @Override
    public void reset() {
        this.log = new Logger();

        this.currentPlayer = null;
        this.currentRoundState = null;
        DEFAULT_PLAYERS.clear();


        this.actionsLeft = ACTION_COUNT;
        this.drawLeft = DRAW_COUNT;
        this.infectionLeft = INFECTION_COUNT;

        if (DEFAULT_INITIAL_STATE) {
            DEFAULT_ROLES.entrySet().stream().forEachOrdered(e -> {
                Player p = new Player(e.getKey(), true);
                p.setRole(e.getValue());
                this.getPlayers().put(e.getKey(), p);
                DEFAULT_PLAYERS.add(p);
            });
        }
        else {
            //save some memory
            //with ui players are initialised in game
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

    }

    /**
     * Move a player to an adjacent city of its current city.
     * Note 1: Only moves over adjacent edges when "careAboutNeighbors" is true, and freely move around when its false.
     * Note 2: When player has AUTO_REMOVE_CUBES_OF_CURED_DISEASE permission, it'll remove all cubes when the player reaches that city and a cure has been found.
     *
     * @param player             - the player to move
     * @param city               - the city the player wants to move to
     * @param careAboutNeighbors - whether or not we can freely move or have to keep edges into account
     * @throws InvalidMoveException - thrown when the move is invalid
     */
    @Override
    public void drive(Player player, City city, IState state, boolean careAboutNeighbors) throws InvalidMoveException {
        if (player.getCity().equals(city) || ((!player.getCity().getNeighbors().contains(city) && careAboutNeighbors))) {
            throw new InvalidMoveException(city, player);
        }

        city.addPawn(player);

        if (player.getRole().events(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE)) {
            city.getCubes().forEach(c -> {
                Cure found = state.getDiseaseRepository().getCures().get(c.getColor());

                if (found != null) {
                    if (found.isDiscovered()) {
                        city.getCubes().removeIf(cb -> cb.getColor().equals(found.getColor()));
                    }
                }
            });
        }

        if(!logged){
            log.addStep(" drive to " + city.getName(), city, player);
        }
        else{
            logged = false;
        }
    }

    @Override
    public void drive(Player player, City city, IState state) throws InvalidMoveException {
        drive(player, city, state, true);

    }

    /**
     * Discard a city card to move to the city named on the card
     * Note 1: We do not direct flight when we are are on a research station.
     * Note 2: We do not direct flight when we are moving to an adjacent city
     * Note 3: We do not direct flight when the player does not have the city card in its hand
     *
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
    public void direct(Player player, City city, IState state) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city, state);
            return;
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city,state);
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

        log.addStep(" direct to " + city.getName(), city, player);
        logged = true;
        drive(player, city, state, false);
    }

    /**
     * Discard the city card that matches the city the player is in to move to any city
     * Note 1: We do not charter flight when we are are on a research station.
     * Note 2: We do not charter flight when we are moving to an adjacent city
     * Note 3: We do not charter flight when the player does not have the city card in its hand
     *
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
    public void charter(Player player, City city, IState state) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city, state);
            return;
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city, state);
            return;
        }

        PlayerCard pc = player.getHand().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(player.getCity());
            }
            return false;
        }).findFirst().orElse(null);

        // If player doesn't have city card of his current city, it can't make this
        // move.
        if (pc == null) {
            throw new InvalidMoveException(city, player);
        }

        player.getHand().remove(pc);

        log.addStep(" charter to " + city.getName(), city, player);
        logged = true;
        drive(player, city, state, false);
    }

    /**
     * Move from a city with a research station to any other city that has a research station.
     * Note 1: Invalid move when you are trying to to shuttle from or to a city without a research station
     * Note 2: Operations expert can move to any city from a research station
     *
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    @Override
    public void shuttle(Player player, City city, IState state) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        if (player.getCity().getResearchStation() == null) {
            throw new InvalidMoveException(city, player);
        } else if (city.getResearchStation() == null) {
            if (!player.getRole().getName().equals("Operations Expert"))
                throw new InvalidMoveException(city, player);
            else {
                state.getBoardRepository().getUsedActions().add(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY);
            }

        }

        log.addStep(" shuttle to " + city.getName(), city, player);
        logged = true;
        drive(player, city, state, false);
    }

    @Override
    public RoundState nextTurn(IState state) {
        return this.nextTurn(this.currentRoundState, state);
    }

    /**
     * Check what the next turn is for the player
     *
     * @param currentState
     * @return
     */
    @Override
    public RoundState nextTurn(RoundState currentState, IState state) {
        if (currentState == null) {
            this.currentRoundState = RoundState.ACTION;
            return this.currentRoundState;
        }
        if (currentState == RoundState.ACTION) {
            actionsLeft--;
            if (actionsLeft <= 0) this.currentRoundState = RoundState.DRAW;
            return this.currentRoundState;
        } else if (currentState == RoundState.DRAW) {
            drawLeft--;
            if (drawLeft <= 0) this.currentRoundState = RoundState.INFECT;
            return this.currentRoundState;
        } else if (currentState == RoundState.INFECT) {
            infectionLeft--;
            if (infectionLeft <= 0) {
                infectionLeft = Objects.requireNonNull(state.getDiseaseRepository().getInfectionRates().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
                this.currentRoundState = null;
                this.nextPlayer();
            }
            return this.currentRoundState;
        }

        throw new IllegalStateException();
    }

    /**
     * Try to treat a disease
     *
     * @param player - the player that wants to treat a disease
     * @param city   - the city to treat the disease in
     * @param color  - The color of the disease to treat
     * @throws Exception - Thrown when a disease couldn't be treated
     */
    @Override
    public void treat(Player player, City city, Color color, IState state) throws Exception {
        if (!player.getCity().equals(city)) {
            throw new Exception("Only able to treat disease in players current city");
        }

        if (city.getCubes().isEmpty()) {
            throw new Exception("There are no disease to be treated in " + city.getName());
        }

        state.getDiseaseRepository().treat(player, city, color);
        log.addStep(" treat in " + city.getName(), city, player);
    }

    @Override
    public void share(Player player, Player target, City city, PlayerCard card, IState state) throws Exception {
        if (city.getPawns().size() <= 1) {
            throw new Exception("Can not share when you are the only pawn in the city");
        }

        RoleAction action = RoleAction.GIVE_PLAYER_CITY_CARD;

        if (player.getRole().actions(action) && state.getBoardRepository().getSelectedRoleAction().equals(action) && !state.getBoardRepository().getUsedActions().contains(action)) {
            state.getBoardRepository().getUsedActions().add(action);
        } else if (!player.getCity().equals(city)) {
            throw new Exception("Only able to share knowledge on the city the player is currently at.");
        }

        if (player.getHand().contains(card)) {
            player.discard(card);
            target.addHand(card);
            return;
        }

        if (!target.getHand().contains(card)) {
            throw new UnableToShareKnowledgeException(city, player, target);
        }

        target.discard(card);
        player.addHand(card);

        log.addStep(" shared " + city.getName() + " with " + target.getName(), city, player);
        nextTurn(this.currentRoundState, state);
    }

    @Override
    public void buildResearchStation(Player player, City city, IState state) throws Exception {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((state.getCityRepository().getResearchStations().size()) >= Settings.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        RoleAction action = RoleAction.BUILD_RESEARCH_STATION;

        if (player.getRole().actions(action) && state.getBoardRepository().getSelectedRoleAction().equals(action) && !state.getBoardRepository().getUsedActions().contains(action)) {
            state.getBoardRepository().getUsedActions().add(action);
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

        state.getCityRepository().addResearchStation(city);
        log.addStep(" build research station in " + city.getName(), city, player);
    }

    @Override
    public void playerAction(ActionType type, IState state, Object... args) throws Exception {
        if (this.getCurrentRoundState() == null) this.nextTurn(state);
        if (currentRoundState.equals(RoundState.ACTION)) {
            if(currentPlayer.isBot()){
                if(currentPlayer.getAgent() == null){
                    currentPlayer.setAgent(new MCTS(state, mctsIterations));
                }
                currentPlayer.getAgent().agentDecision(state);
            }
            if (type == null) type = ActionType.NO_ACTION;
            if (state.getBoardRepository().getSelectedRoleAction() == null)
                state.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
            if (!type.equals(ActionType.SKIP_ACTION)) {
                City city = state.getBoardRepository().getSelectedCity();
                Player player = this.currentPlayer;

                IBoardRepository boardRepository = state.getBoardRepository();

                if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction().equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                    if (args.length == 0) {
                        throw new Exception("You need to select an event Card from the discard pile.");
                    }

                    Object found = args[0];

                    try {
                        PlayerCard card = (PlayerCard) found;

                        if (!state.getCardRepository().getEventDiscardPile().contains(card)) {
                            throw new Exception("You need to select an event Card from the discard pile.");
                        }

                        state.getCardRepository().getEventDiscardPile().remove(card);
                        player.addHand(card);
                        GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                    } catch (Exception e) {
                        throw new Exception("You need to select an event Card from the discard pile.");
                    }
                }
                else if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction()
                        .equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY) || type.equals(ActionType.SHUTTLE)) {
                    shuttle(player, city, state);
                    state.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
                    GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                } else if (boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction().equals(RoleAction.BUILD_RESEARCH_STATION)
                        || type.equals(ActionType.BUILD_RESEARCH_STATION)) {
                    state.getPlayerRepository().buildResearchStation(player, city, state);
                    state.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
                    GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                } else if (type.equals(ActionType.DRIVE)) {
                    drive(player, city, state);
                    GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                }
                else if (type.equals(ActionType.DIRECT_FLIGHT)) {
                    direct(player, city, state);
                    GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                }
                else if (type.equals(ActionType.CHARTER_FLIGHT)) {
                    charter(player, city, state);
                    GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                }
                else if (type.equals(ActionType.TREAT_DISEASE)) {
                    if (args.length <= 0) {
                        throw new Exception("You need to provide the disease to treat.");
                    }
                    Object found = args[0];
                    try {
                        Color color = (Color) found;
                        treat(player, city, color, state);
                        GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                    } catch (Exception e) {
                        throw new Exception("You need to provide the disease to treat.");
                    }
                } else if (type.equals(ActionType.SHARE_KNOWLEDGE)
                        || boardRepository.getSelectedRoleAction() != null &&
                        state.getBoardRepository().getSelectedRoleAction().equals(RoleAction.GIVE_PLAYER_CITY_CARD)) {
                    if (args.length <= 0) {
                        throw new Exception("You need to provide the player to negotiate with, the card you want, and whether you are giving that card.");
                    }

                    Object targetObj = args[0];
                    Object cardObj = args[1];

                    try {
                        Player target = (Player) targetObj;
                        PlayerCard card = (PlayerCard) cardObj;
                        share(player, target, city, card, state);
                        state.getBoardRepository().setSelectedRoleAction(RoleAction.NO_ACTION);
                        GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                    } catch (Exception e) {
                        throw new Exception("You need to provide the player to negotiate with, the card you want, and whether you are giving that card.");
                    }

                }
                else if (type.equals(ActionType.DISCOVER_CURE)) {
                    if (args.length <= 0) {
                        throw new Exception("You need to select a cure to discover.");
                    }
                    Object found = args[0];
                    try {
                        Cure cure = (Cure) found;
                        state.getDiseaseRepository().discoverCure(player, cure);
                        GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markActionTypeUsed(type);
                    } catch (Exception e) {
                        throw new Exception("You need to select a cure to discover");
                    }
                }
            }
           this.nextTurn(state);

        } else if (currentRoundState.equals(RoundState.DRAW)) {
            state.getCardRepository().drawPlayerCard(state,
                    getCurrentPlayer().getHand().size() >= HAND_LIMIT ?
                            state.getDiscardingCard() : new PlayerCard[]{}
            );
            this.nextTurn(state);
            if (drawLeft >= 0) {
                this.playerAction(null,state);
            }
        } else if (currentRoundState.equals(RoundState.INFECT)) {
            state.getCardRepository().drawInfectionCard(state);
            this.nextTurn(state);
            if (infectionLeft >= 0 && currentRoundState != null) {
                this.playerAction(null,state);
            }
        }
    }

    public void createPlayer(String name, boolean isBot) throws PlayerLimitException {
        if (this.players.size() + 1 > Settings.PLAYER_THRESHOLD) {
            throw new PlayerLimitException();
        }

        Player player = new Player(name, isBot);
        this.players.put(name, player);
    }

    @Override
    public void nextPlayer() {
        Player nextPlayer = this.playerOrder.poll();

        this.playerOrder.add(nextPlayer);
        this.setCurrentPlayer(this.playerOrder.peek());

        this.resetRound();
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
    public Logger getLog() {
        return log;
    }
}
