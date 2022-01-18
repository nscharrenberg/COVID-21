package org.um.nine.headless.game.exceptions;


import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

public class NoDiseaseOrOutbreakPossibleDueToEvent extends Exception {
    private final City city;

    public NoDiseaseOrOutbreakPossibleDueToEvent(City city) {
        super("Prevented a disease from being added or outbreak from happening in " + city.getName());
        this.city = city;
    }

    public NoDiseaseOrOutbreakPossibleDueToEvent(Player player, City city) {
        super(player.getName() + " prevented a disease from being added or outbreak from happening in " + city.getName() + " [" + player.getRole().getName() + "]");
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}
