package org.um.nine.agents.Rhea;

import org.um.nine.utils.versioning.State;

public interface StateHeuristic {
    double evaluateState(State state);
}
