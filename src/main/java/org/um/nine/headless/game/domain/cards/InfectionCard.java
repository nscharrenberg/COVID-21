package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.City;

public class InfectionCard extends Card {
    private City city;

    @Override
    public InfectionCard clone() {
        InfectionCard clone = (InfectionCard) super.clone();
        clone.setCity(this.getCity());
        return clone;
    }

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
