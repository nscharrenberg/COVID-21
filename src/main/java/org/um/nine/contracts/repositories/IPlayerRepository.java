package org.um.nine.contracts.repositories;

import org.um.nine.domain.Player;
import org.um.nine.exceptions.PlayerLimitException;

import java.util.HashMap;

public interface IPlayerRepository {
    void addPlayer(Player player) throws PlayerLimitException;
    HashMap<String, Player> getPlayers();
    void reset();
}
