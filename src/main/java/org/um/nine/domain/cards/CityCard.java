package org.um.nine.domain.cards;

import org.um.nine.domain.Card;
import org.um.nine.domain.City;

public class CityCard extends PlayerCard {
    private City city;

    public CityCard(City city) {
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
