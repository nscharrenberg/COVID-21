package org.um.nine.v1.contracts.repositories;

import org.um.nine.v1.domain.ActionType;
import org.um.nine.v1.domain.City;
import org.um.nine.v1.domain.Player;
import org.um.nine.v1.domain.RoundState;
import org.um.nine.v1.domain.roles.RoleAction;
import org.um.nine.v1.exceptions.*;

import java.util.HashMap;

public interface IPlayerRepository {
    void addPlayer(Player player) throws PlayerLimitException;
    HashMap<String, Player> getPlayers();

    void reset();

    void drive(Player player, City city) throws InvalidMoveException;

    void drive(Player player, City city, boolean careAboutNeighbours) throws InvalidMoveException;

    void direct(Player player, City city) throws InvalidMoveException;

    void charter(Player player, City city) throws InvalidMoveException;

    void shuttle(Player player, City city) throws InvalidMoveException;

    void verifyExternalMove(Player instigator, Player target, City city, boolean accept) throws InvalidMoveException, ExternalMoveNotAcceptedException;

    RoundState nextState(RoundState currentState);

    RoundState getCurrentRoundState();

    void setCurrentRoundState(RoundState currentRoundState);

    void assignRoleToPlayer(Player player);

    Player getCurrentPlayer();

    void setCurrentPlayer(Player currentPlayer);

    void nextPlayer();

    void resetRound();

    void decidePlayerOrder();

    void share(Player player, City city);

    void treat(Player player, City city);

    void action(ActionType type) throws InvalidMoveException, NoActionSelectedException, ResearchStationLimitException, CityAlreadyHasResearchStationException, NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;

    void roleAction(RoleAction roleAction, Player player);

    void cleanup();

    void agentDecision();
}
