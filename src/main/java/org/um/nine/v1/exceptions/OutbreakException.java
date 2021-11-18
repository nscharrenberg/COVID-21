package org.um.nine.v1.exceptions;

import org.um.nine.v1.domain.City;

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
