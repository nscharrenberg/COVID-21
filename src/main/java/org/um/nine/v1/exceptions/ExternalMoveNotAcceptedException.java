package org.um.nine.v1.exceptions;

import org.um.nine.v1.domain.City;
import org.um.nine.v1.domain.Player;

public class ExternalMoveNotAcceptedException extends Exception {
    private final Player instigator;
    private final Player target;
    private final City city;

    public ExternalMoveNotAcceptedException(Player instigator, Player target, City city) {
        super(target.getName() + "did NOT accept " + instigator.getName() + "s request to move him to " + city.getName());

        this.instigator = instigator;
        this.target = target;
        this.city = city;
    }

    public Player getInstigator() {
        return instigator;
    }

    public Player getTarget() {
        return target;
    }

    public City getCity() {
        return city;
    }
}
