package org.um.nine.headless.game.exceptions;


import org.um.nine.headless.game.domain.City;

public class NoCityCardToTreatDiseaseException extends Exception {
    private City city;

    public NoCityCardToTreatDiseaseException(City city) {
        super("Player does not have a city card to treat " + city.getName());
        this.city = city;
    }
}
