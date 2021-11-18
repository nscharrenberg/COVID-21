package org.um.nine.headless.game.exceptions;


import org.um.nine.headless.game.domain.City;

public class OutbreakException extends Exception {
    private City city;
    public OutbreakException(City city) {
        super("An outbreak has occured.");
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}
