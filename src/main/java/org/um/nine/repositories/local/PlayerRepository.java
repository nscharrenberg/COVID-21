package org.um.nine.repositories.local;

import org.um.nine.Info;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.PlayerLimitException;

import java.util.HashMap;

public class PlayerRepository implements IPlayerRepository {
    private HashMap<String, Player> players;

    public PlayerRepository() {
        reset();
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) throws PlayerLimitException {
        if (this.players.size() + 1 <= Info.PLAYER_THRESHOLD) {
            this.players.put(player.getName(), player);
        }

        throw new PlayerLimitException();
    }

    public void reset() {
        this.players = new HashMap<>();
    }
}
