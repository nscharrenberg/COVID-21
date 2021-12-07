package org.um.nine.jme.repositories;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.*;
import org.um.nine.headless.game.exceptions.*;

import java.util.*;

public class PlayerRepository {
    public PlayerRepository() {
    }

    /**
     * Resets state to its original data
     */
    public void reset() {
        GameStateFactory.getInitialState().getPlayerRepository().reset();
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
        GameStateFactory.getInitialState().getPlayerRepository().drive(player, city, careAboutNeighbors);
    }

    public void drive(Player player, City city) throws InvalidMoveException {
        GameStateFactory.getInitialState().getPlayerRepository().drive(player, city);
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
        GameStateFactory.getInitialState().getPlayerRepository().direct(player, city);
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
        GameStateFactory.getInitialState().getPlayerRepository().charter(player, city);
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
        GameStateFactory.getInitialState().getPlayerRepository().shuttle(player, city);
    }

    public RoundState nextTurn() {
        return GameStateFactory.getInitialState().getPlayerRepository().nextTurn();
    }

    /**
     * Check what the next turn is for the player
     * 
     * @param currentState
     * @return
     */
    public RoundState nextTurn(RoundState currentState) {
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
        GameStateFactory.getInitialState().getPlayerRepository().treat(player, city, color);
    }

    public void share(Player player, Player target, City city, PlayerCard card) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().share(player, target, city, card);
    }

    public void buildResearchStation(Player player, City city) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().buildResearchStation(player, city);
    }

    public void playerAction(ActionType type, Object... args) throws Exception {
        GameStateFactory.getInitialState().getPlayerRepository().playerAction(type, args);
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
}
