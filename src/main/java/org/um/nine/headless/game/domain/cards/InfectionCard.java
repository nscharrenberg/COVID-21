package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.City;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfectionCard that = (InfectionCard) o;

        return Objects.equals(city, that.city);
    }


}
