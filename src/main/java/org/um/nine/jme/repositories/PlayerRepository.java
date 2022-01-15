package org.um.nine.jme.repositories;

import com.jme3.math.Vector3f;
import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.*;
import org.um.nine.jme.screens.DialogBoxState;
import org.um.nine.jme.screens.dialogs.DiscoverCureDialogBox;
import org.um.nine.jme.screens.dialogs.ShareCityCardDialogBox;
import org.um.nine.jme.screens.dialogs.TreatDiseaseDialogBox;
import org.um.nine.jme.screens.hud.ContingencyPlannerState;
import org.um.nine.jme.screens.hud.PlayerInfoState;
import org.um.nine.jme.utils.JmeFactory;

import java.util.*;

public class PlayerRepository {
    public PlayerRepository() {
    }

    private BoardRepository boardRepository = JmeFactory.getBoardRepository();

    private ContingencyPlannerState contingencyPlannerState = JmeFactory.getContingencyPlannerState();

    private GameRepository gameRepository = JmeFactory.getGameRepository();

    private CityRepository cityRepository = JmeFactory.getCityRepository();

    private TreatDiseaseDialogBox treatDiseaseDialogBox = JmeFactory.getTreatDiseaseDialogBox();

    private DiscoverCureDialogBox discoverCureDialogBox = JmeFactory.getDiscoverCureDialogBox();

    private ShareCityCardDialogBox shareCityCardDialogBox = JmeFactory.getShareCityCardDialogBox();

    private CardRepository cardRepository = JmeFactory.getCardRepository();

    private PlayerInfoState playerInfoState = JmeFactory.getPlayerInfoState();

    private VisualRepository visualRepository = JmeFactory.getVisualRepository();

    private IState state = GameStateFactory.getInitialState();

    /**
     * Resets state to its original data
     */
    public void reset() {
        this.boardRepository = JmeFactory.getBoardRepository();
        this.contingencyPlannerState = JmeFactory.getContingencyPlannerState();
        this.gameRepository = JmeFactory.getGameRepository();
        this.cityRepository = JmeFactory.getCityRepository();
        this.treatDiseaseDialogBox = JmeFactory.getTreatDiseaseDialogBox();
        this.discoverCureDialogBox = JmeFactory.getDiscoverCureDialogBox();
        this.shareCityCardDialogBox = JmeFactory.getShareCityCardDialogBox();
        this.cardRepository = JmeFactory.getCardRepository();
        this.playerInfoState = JmeFactory.getPlayerInfoState();
        this.visualRepository = JmeFactory.getVisualRepository();
        GameStateFactory.getInitialState().getPlayerRepository().reset();
        state = GameStateFactory.getInitialState();
    }

    /**
     * Move a player to an adjacent city of its current city.
     * Note 1: Only moves over adjacent edges when "careAboutNeighbors" is true, and
     * freely move around when its false.
     * Note 2: When player has AUTO_REMOVE_CUBES_OF_CURED_DISEASE permission, it'll
     * remove all cubes when the player reaches that city and a cure has been found.
     * 
     * @param player             - the player to move
     * @param city               - the city the player wants to move to
     * @param careAboutNeighbors - whether or not we can freely move or have to keep
     *                           edges into account
     * @throws InvalidMoveException - thrown when the move is invalid
     */
    public void drive(Player player, City city, boolean careAboutNeighbors) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().drive(player, city, state, careAboutNeighbors);
        visualRepository.renderPlayer(player, city.getPawnPosition(player));
    }

    public void drive(Player player, City city) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().drive(player, city, state);
        visualRepository.renderPlayer(player, city.getPawnPosition(player));
    }

    /**
     * Discard a city card to move to the city named on the card
     * Note 1: We do not direct flight when we are are on a research station.
     * Note 2: We do not direct flight when we are moving to an adjacent city
     * Note 3: We do not direct flight when the player does not have the city card
     * in its hand
     * 
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    public void direct(Player player, City city) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().direct(player, city, state);
        visualRepository.renderPlayer(player, city.getPawnPosition(player));
    }

    /**
     * Discard the city card that matches the city the player is in to move to any
     * city
     * Note 1: We do not charter flight when we are are on a research station.
     * Note 2: We do not charter flight when we are moving to an adjacent city
     * Note 3: We do not charter flight when the player does not have the city card
     * in its hand
     * 
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    public void charter(Player player, City city) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().charter(player, city, state);
        visualRepository.renderPlayer(player, city.getPawnPosition(player));
    }

    /**
     * Move from a city with a research station to any other city that has a
     * research station.
     * Note 1: Invalid move when you are trying to to shuttle from or to a city
     * without a research station
     * Note 2: Operations expert can move to any city from a research station
     * 
     * @param player - the player to move
     * @param city   - the city the player wants to go to
     * @throws InvalidMoveException - thrown when the player does not have the card
     */
    public void shuttle(Player player, City city) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().shuttle(player, city, state);
        visualRepository.renderPlayer(player, city.getPawnPosition(player));
    }

    /**
     * Check what the next turn is for the player
     * 
     * @param currentState
     * @return
     */
    public RoundState nextTurn(IState currentState) {
        return GameStateFactory.getInitialState().getPlayerRepository().nextTurn(currentState);
    }

    /**
     * Try to treat a disease
     * 
     * @param player - the player that wants to treat a disease
     * @param city   - the city to treat the disease in
     * @param color  - The color of the disease to treat
     * @throws Exception - Thrown when a disease couldn't be treated
     */
    public void treat(Player player, City city, Color color) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().treat(player, city, color, state);
    }

    public void treat(Player player, City city) throws Exception {
        gameRepository.getApp().getStateManager().attach(treatDiseaseDialogBox);
        treatDiseaseDialogBox.setPlayer(player);
        treatDiseaseDialogBox.setCity(city);
        treatDiseaseDialogBox.setHeartbeat(true);
        treatDiseaseDialogBox.setEnabled(true);
    }

    public void share(Player player, City city) throws Exception {
        if (city.getPawns().size() <= 1) {
            DialogBoxState dialog = new DialogBoxState("Can not share when you are the only pawn in the city.");
            gameRepository.getApp().getStateManager().attach(dialog);
            dialog.setEnabled(true);
            return;
        }

        RoleAction action = RoleAction.GIVE_PLAYER_CITY_CARD;
        if (player.getRole().actions(action) && boardRepository.getSelectedRoleAction().equals(action)
                && !boardRepository.getUsedActions().contains(action)) {
            boardRepository.getUsedActions().add(action);
        } else {
            if (!player.getCity().equals(city)) {
                DialogBoxState dialog = new DialogBoxState(
                        "Only able to share knowledge on the city the player is currently at.");
                gameRepository.getApp().getStateManager().attach(dialog);
                dialog.setEnabled(true);
                return;
            }
        }

        gameRepository.getApp().getStateManager().attach(shareCityCardDialogBox);
        shareCityCardDialogBox.setHeartbeat(true);
        shareCityCardDialogBox.setCity(city);
        shareCityCardDialogBox.setCurrentPlayer(player);
        shareCityCardDialogBox.setEnabled(true);
    }

    public void buildResearchStation(Player player, City city) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().buildResearchStation(player, city, state);
    }

    public void playerAction(ActionType type, Object... args) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().playerAction(type, state, args);
    }

    public void createPlayer(String name, boolean isBot) throws PlayerLimitException {
        GameStateFactory.getInitialState().getPlayerRepository().createPlayer(name, isBot);
    }

    public void nextPlayer() {
        GameStateFactory.getInitialState().getPlayerRepository().nextPlayer();
    }

    public void resetRound() {
        GameStateFactory.getInitialState().getPlayerRepository().resetRound();
    }

    public void decidePlayerOrder() {
        GameStateFactory.getInitialState().getPlayerRepository().decidePlayerOrder();
    }

    public void assignRoleToPlayer(Player player) {
        GameStateFactory.getInitialState().getPlayerRepository().assignRoleToPlayer(player);
    }

    public void roleAction(RoleAction roleAction, Player player) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().roleAction(roleAction, player);
    }

    public HashMap<String, Player> getPlayers() {
        return GameStateFactory.getInitialState().getPlayerRepository().getPlayers();
    }

    public void setPlayers(HashMap<String, Player> players) {
        GameStateFactory.getInitialState().getPlayerRepository().setPlayers(players);
    }

    public Stack<Role> getAvailableRoles() {
        return GameStateFactory.getInitialState().getPlayerRepository().getAvailableRoles();
    }

    public void setAvailableRoles(Stack<Role> availableRoles) {
        GameStateFactory.getInitialState().getPlayerRepository().setAvailableRoles(availableRoles);
    }

    public Player getCurrentPlayer() {
        return GameStateFactory.getInitialState().getPlayerRepository().getCurrentPlayer();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        GameStateFactory.getInitialState().getPlayerRepository().setCurrentPlayer(currentPlayer);
    }

    public Queue<Player> getPlayerOrder() {
        return GameStateFactory.getInitialState().getPlayerRepository().getPlayerOrder();
    }

    public void setPlayerOrder(Queue<Player> playerOrder) {
        GameStateFactory.getInitialState().getPlayerRepository().setPlayerOrder(playerOrder);
    }

    public RoundState nextState(RoundState currentState) {
        if (currentState == null) {
            this.setCurrentRoundState(RoundState.ACTION);
            return RoundState.ACTION;
        } else if (currentState == RoundState.ACTION) {
            setActionsLeft(getActionsLeft() - 1);
            if (getActionsLeft() == 0) {
                this.setCurrentRoundState(RoundState.DRAW);
                return RoundState.DRAW;
            }

            this.setCurrentRoundState(RoundState.ACTION);
            return RoundState.ACTION;
        } else if (currentState == RoundState.DRAW) {
            setDrawLeft(getDrawLeft() - 1);
            if (getDrawLeft() == 0) {
                this.setCurrentRoundState(RoundState.INFECT);
                return RoundState.INFECT;
            }
            this.setCurrentRoundState(RoundState.DRAW);
            return RoundState.DRAW;
        } else if (currentState == RoundState.INFECT) {
            setInfectionLeft(getInfectionLeft() - 1);
            if (getInfectionLeft() == 0) {
                setInfectionLeft(Objects.requireNonNull(JmeFactory.getDiseaseRepository().getInfectionRates().stream()
                        .filter(Marker::isCurrent).findFirst().orElse(null)).getCount());
                this.setCurrentRoundState(null);
                nextPlayer();
                return null;
            }

            this.setCurrentRoundState(RoundState.INFECT);
            return RoundState.INFECT;
        }

        throw new IllegalStateException();
    }

    public RoundState getCurrentRoundState() {
        return GameStateFactory.getInitialState().getPlayerRepository().getCurrentRoundState();
    }

    public void setCurrentRoundState(RoundState currentRoundState) {
        GameStateFactory.getInitialState().getPlayerRepository().setCurrentRoundState(currentRoundState);
    }

    public int getActionsLeft() {
        return GameStateFactory.getInitialState().getPlayerRepository().getActionsLeft();
    }

    public void setActionsLeft(int actionsLeft) {
        GameStateFactory.getInitialState().getPlayerRepository().setActionsLeft(actionsLeft);
    }

    public int getDrawLeft() {
        return GameStateFactory.getInitialState().getPlayerRepository().getDrawLeft();
    }

    public void setDrawLeft(int drawLeft) {
        GameStateFactory.getInitialState().getPlayerRepository().setDrawLeft(drawLeft);
    }

    public int getInfectionLeft() {
        return GameStateFactory.getInitialState().getPlayerRepository().getInfectionLeft();
    }

    public void setInfectionLeft(int infectionLeft) {
        GameStateFactory.getInitialState().getPlayerRepository().setInfectionLeft(infectionLeft);
    }

    public void action(ActionType type)
            throws InvalidMoveException, NoActionSelectedException, ResearchStationLimitException,
            CityAlreadyHasResearchStationException, NoCubesLeftException,
            NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        boolean skipClicked = false;
        if (getCurrentRoundState() == null) {
            nextState(null);
        }

        if (!getCurrentPlayer().isBot()) {
            if (getCurrentRoundState().equals(RoundState.ACTION)) {
                if (type == null && boardRepository.getSelectedRoleAction() == null) {
                    throw new NoActionSelectedException();
                }

                City city = boardRepository.getSelectedCity();
                Player player = getCurrentPlayer();

                if (type == null) {
                    type = ActionType.NO_ACTION;
                } else if (boardRepository.getSelectedRoleAction() == null) {
                    boardRepository.setSelectedRoleAction(RoleAction.NO_ACTION);
                }

                if (boardRepository.getSelectedRoleAction().equals(RoleAction.TAKE_ANY_DISCARED_EVENT)) {
                    gameRepository.getApp().getStateManager().attach(contingencyPlannerState);
                    contingencyPlannerState.setHeartbeat(true);
                    contingencyPlannerState.setEnabled(true);
                    boardRepository.setSelectedRoleAction(RoleAction.NO_ACTION);
                } else if (boardRepository.getSelectedRoleAction()
                        .equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY)
                        || type.equals(ActionType.SHUTTLE)) {
                    shuttle(player, city);
                    boardRepository.setSelectedRoleAction(RoleAction.NO_ACTION);
                } else if (boardRepository.getSelectedRoleAction().equals(RoleAction.BUILD_RESEARCH_STATION)
                        || type.equals(ActionType.BUILD_RESEARCH_STATION)) {
                    try {
                        cityRepository.addResearchStation(city, player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boardRepository.setSelectedRoleAction(RoleAction.NO_ACTION);
                } else if (type.equals(ActionType.DRIVE)) {
                    drive(player, city);
                } else if (type.equals(ActionType.DIRECT_FLIGHT)) {
                    direct(player, city);
                } else if (type.equals(ActionType.CHARTER_FLIGHT)) {
                    charter(player, city);
                } else if (type.equals(ActionType.TREAT_DISEASE)) {
                    try {
                        treat(player, city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return;
                } else if (type.equals(ActionType.SHARE_KNOWLEDGE)
                        || boardRepository.getSelectedRoleAction().equals(RoleAction.GIVE_PLAYER_CITY_CARD)) {
                    try {
                        share(player, city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boardRepository.setSelectedRoleAction(RoleAction.NO_ACTION);
                    return;
                } else if (type.equals(ActionType.DISCOVER_CURE)) {
                    if (!player.getCity().equals(city)) {
                        DialogBoxState dialog = new DialogBoxState(
                                "Only able to discover cure in players current city");
                        gameRepository.getApp().getStateManager().attach(dialog);
                        dialog.setEnabled(true);
                        return;
                    }

                    discoverCureDialogBox.setPlayer(player);
                    gameRepository.getApp().getStateManager().attach(discoverCureDialogBox);
                    discoverCureDialogBox.setEnabled(true);

                    return;
                } else if (type.equals(ActionType.SKIP_ACTION)) {
                    skipClicked = true;
                    nextState(getCurrentRoundState());
                }
                if (!skipClicked) {
                    nextState(getCurrentRoundState());
                }
                skipClicked = false;
            } else if (getCurrentRoundState().equals(RoundState.DRAW)) {
                cardRepository.drawPlayerCard(state);

                nextState(getCurrentRoundState());
                if (getDrawLeft() >= 0) {

                    action(null);
                }

            } else if (getCurrentRoundState().equals(RoundState.INFECT)) {
                cardRepository.drawInfectionCard(state);

                nextState(getCurrentRoundState());
                if (getInfectionLeft() >= 0) {
                    boardRepository.setSelectedRoleAction(null);
                    action(null);
                }
            }

            playerInfoState.setHeartbeat(true);
        } else {
            Player currentPlayer = GameStateFactory.getInitialState().getPlayerRepository().getCurrentPlayer();
            if(currentPlayer.getAgent() == null) currentPlayer.setAgent(new MCTS(GameStateFactory.getInitialState(),100));
            try{
                currentPlayer.getAgent().agentDecision(GameStateFactory.getInitialState());
                /* todo implementation of updates when using ai
                LinkedList<Logger.LogRecord> log = currentPlayer.getAgent().getLog().getLog();
                String a = log.getLast().action();
                City c = log.getLast().targetLocation();
                Player p = log.getLast().player();

                switch (a){
                    case "drive":
                        visualRepository.renderPlayer(p, c.getPawnPosition(p));
                    case "charter":
                        visualRepository.renderPlayer(p, c.getPawnPosition(p));
                    case "direct":
                        visualRepository.renderPlayer(p, c.getPawnPosition(p));
                    case "shuttle":
                        visualRepository.renderPlayer(p, c.getPawnPosition(p));
                    case "research station":
                        visualRepository.renderResearchStation(c.getResearchStation(),new Vector3f(-20, 5, 0));
                }*/
            }catch (Exception e){
                e.printStackTrace();
            }
            cardRepository.drawPlayerCard(state);
            nextState(getCurrentRoundState());
            cardRepository.drawPlayerCard(state);
            nextState(getCurrentRoundState());
            cardRepository.drawInfectionCard(state);
            nextState(getCurrentRoundState());
            cardRepository.drawInfectionCard(state);
            nextState(getCurrentRoundState());
            boardRepository.setSelectedRoleAction(null);

        }

    }
}
