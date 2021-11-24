package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.IState;

public interface StateHeuristic {

    double cp = 0.1;
    double evaluateState(IState state);

    default double evaluateFitness(IState state){
        if (state.gameOver()){
            if (state.isVictory()) return 1;
            return 0;
        }
        return evaluateState(state);
    }

    default double evaluateFitness_penalize(IState state){
        if (state.gameOver()){
            if (state.isVictory()) return 1;
            return cp * evaluateState(state) ;
        }
        return evaluateState(state);
    }
}
