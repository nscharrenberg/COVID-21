package org.um.nine.v1.domain.cards;

import org.um.nine.v1.domain.Card;
import org.um.nine.v1.domain.City;

public class InfectionCard extends Card {
    private City city;

    public InfectionCard(City city) {
        super(city.getName());
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
