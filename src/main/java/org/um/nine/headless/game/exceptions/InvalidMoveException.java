package org.um.nine.headless.game.exceptions;

import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

public class InvalidMoveException extends Exception {
    private final City city;
    private final Player player;

    public InvalidMoveException(City city, Player player) {
        super("Unable to move " + player.getName() + " from " + player.getCity().getName() + " to " + city.getName());
        this.city = city;
        this.player = player;
    }

    public City getCity() {
        return city;
    }

    public Player getPlayer() {
        return player;
    }
}
