package org.um.nine.headless.agents.state;

public interface StateHeuristic {

    double cp = 0.1;
    double evaluateState(IState state);

    default double evaluateFitness(IState state){
        if (state.isGameLost()){
            if (state.isGameWon()) return 1;
            return 0;
        }
        return evaluateState(state);
    }

    default double evaluateFitnessPenalize(IState state){
        if (state.isGameLost()){
            if (state.isGameWon()) return 1;
            return cp * evaluateState(state) ;
        }
        return evaluateState(state);
    }
}
