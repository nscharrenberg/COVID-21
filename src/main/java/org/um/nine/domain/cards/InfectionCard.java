package org.um.nine.domain.cards;

import org.um.nine.domain.Card;
import org.um.nine.domain.City;

public class InfectionCard extends Card {
    private City city;

    public InfectionCard(String name, City city) {
        super(name);
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}