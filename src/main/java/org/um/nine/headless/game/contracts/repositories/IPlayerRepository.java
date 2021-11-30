package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.agents.Log;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.InvalidMoveException;
import org.um.nine.headless.game.exceptions.PlayerLimitException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

public interface IPlayerRepository {
    void reset();

    void drive(Player player, City city, boolean careAboutNeighbors) throws InvalidMoveException;

    void drive(Player player, City city) throws InvalidMoveException;

    void direct(Player player, City city) throws InvalidMoveException;

    void charter(Player player, City city) throws InvalidMoveException;

    void shuttle(Player player, City city) throws InvalidMoveException;

    RoundState nextTurn();

    RoundState nextTurn(RoundState currentState);

    void treat(Player player, City city, Color color) throws Exception;

    void share(Player player, Player target, City city, PlayerCard card) throws Exception;

    void buildResearchStation(Player player, City city) throws Exception;

    void playerAction(ActionType type, Objects... args) throws Exception;

    void nextPlayer();

    void resetRound();

    void decidePlayerOrder();

    void assignRoleToPlayer(Player player);

    void roleAction(RoleAction roleAction, Player player) throws Exception;

    HashMap<String, Player> getPlayers();

    void setPlayers(HashMap<String, Player> players);

    Stack<Role> getAvailableRoles();

    void setAvailableRoles(Stack<Role> availableRoles);

    Player getCurrentPlayer();

    void setCurrentPlayer(Player currentPlayer);

    Queue<Player> getPlayerOrder();

    void setPlayerOrder(Queue<Player> playerOrder);

    RoundState getCurrentRoundState();

    void setCurrentRoundState(RoundState currentRoundState);

    int getActionsLeft();

    void setActionsLeft(int actionsLeft);

    int getDrawLeft();

    void setDrawLeft(int drawLeft);

    int getInfectionLeft();

    void setInfectionLeft(int infectionLeft);

    void createPlayer(String name, boolean isBot) throws PlayerLimitException;

    Log getLog();
}
