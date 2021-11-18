package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.City;

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
