package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.State;

public interface StateHeuristic {

    double cp = 0.1;
    double evaluateState(State state);

    default double evaluateFitness(State state){
        if (state.gameOver()){
            if (state.isVictory()) return 1;
            return 0;
        }
        return evaluateState(state);
    }

    default double evaluateFitness_penalize(State state){
        if (state.gameOver()){
            if (state.isVictory()) return 1;
            return cp * evaluateState(state) ;
        }
        return evaluateState(state);
    }
}
