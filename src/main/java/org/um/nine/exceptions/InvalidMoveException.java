package org.um.nine.exceptions;

import org.um.nine.domain.City;
import org.um.nine.domain.Player;

public class InvalidMoveException extends Exception {
    private final City city;
    private final Player player;

    public InvalidMoveException(City city, Player player) {
        super("Unable to move " + player.getName() + " to " + city.getName());
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
