package org.um.nine.headless.agents.state;

public interface StateHeuristic {


    double cp = 0.1;

    double evaluateState(IState state);


    static double w(StateHeuristic f, IState state) {
        if (state.isGameLost()) {
            if (state.isGameWon()) return 1;
            return 0;
        }
        return f.evaluateState(state);
    }


    static double p(StateHeuristic f, IState state) {
        if (state.isGameLost()) {
            if (state.isGameWon()) return 1;
            return cp * f.evaluateState(state);
        }
        return f.evaluateState(state);
    }

}
