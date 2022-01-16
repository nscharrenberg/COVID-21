package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.City;

public class CityCard extends PlayerCard {
    private City city;

    @Override
    public CityCard clone() {
        CityCard clone = (CityCard) super.clone();
        clone.setCity(this.getCity());
        return clone;
    }

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
