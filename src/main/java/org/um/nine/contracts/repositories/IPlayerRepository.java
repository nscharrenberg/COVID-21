package org.um.nine.contracts.repositories;

import org.um.nine.domain.ActionType;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.RoundState;
import org.um.nine.exceptions.*;

import java.util.HashMap;

public interface IPlayerRepository {
    void addPlayer(Player player) throws PlayerLimitException;
    HashMap<String, Player> getPlayers();

    void reset();

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

    void action(ActionType type) throws InvalidMoveException, NoActionSelectedException, ResearchStationLimitException, CityAlreadyHasResearchStationException;
}
