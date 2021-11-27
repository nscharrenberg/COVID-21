package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;

public record MovingAction(ActionType action, City fromCity, City toCity) {

    @Override
    public String toString() {
        String lbl = switch (action){
            case DRIVE -> "w";
            case SHUTTLE -> "s";
            default -> "?";
        };
        return "{" + toCity.getName() + " <-"+lbl+"- " + fromCity.getName() + "}";
    }
}
