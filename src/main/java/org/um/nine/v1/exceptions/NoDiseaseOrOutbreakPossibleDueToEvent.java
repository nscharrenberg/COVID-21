package org.um.nine.v1.exceptions;

import org.um.nine.v1.domain.City;

public class NoDiseaseOrOutbreakPossibleDueToEvent extends Exception {
    private final City city;

    public NoDiseaseOrOutbreakPossibleDueToEvent(City city) {
        super("Prevented a disease from being added or outbreak from happening in " + city.getName());
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}
