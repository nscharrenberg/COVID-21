package org.um.nine.exceptions;

import org.um.nine.domain.City;

public class NoDiseaseOrOutbreakPossibleDueToEvent extends Exception {
    private City city;

    public NoDiseaseOrOutbreakPossibleDueToEvent(City city) {
        super("Prevented a disease from being added or outbreak from happening in " + city.getName());
        this.city = city;
    }

    public City getCity() {
        return city;
    }
}
