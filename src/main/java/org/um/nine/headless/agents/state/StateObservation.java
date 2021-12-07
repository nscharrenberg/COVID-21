package org.um.nine.headless.agents.state;

import org.um.nine.headless.game.domain.ActionType;

import java.util.List;

public interface StateObservation {

    List<ActionType> getAvailableActions();
    boolean isGameOver();
    void advance(ActionType action);
    StateObservation copy();
}
