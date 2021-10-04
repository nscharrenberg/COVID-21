package org.um.nine.exceptions;

import org.um.nine.domain.City;
import org.um.nine.domain.Disease;

public class NoCityCardToTreatDiseaseException extends Exception {
    private City city;

    public NoCityCardToTreatDiseaseException(City city) {
        super("Player does not have a city card to treat " + city.getName());
        this.city = city;
    }
}
