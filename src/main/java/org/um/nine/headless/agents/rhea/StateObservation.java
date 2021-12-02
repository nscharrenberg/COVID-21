package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.game.domain.actions.ActionType;

import java.util.List;

public interface StateObservation {

    List<ActionType> getAvailableActions();
    boolean isGameOver();
    void advance(ActionType action);
    StateObservation copy();
}
