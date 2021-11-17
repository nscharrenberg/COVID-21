package org.um.nine.agents.Rhea;

import org.um.nine.domain.ActionType;

import java.util.List;

public interface StateObservation {

    List<ActionType> getAvailableActions();
    boolean isGameOver();
    void advance(ActionType action);
    StateObservation copy();
}
