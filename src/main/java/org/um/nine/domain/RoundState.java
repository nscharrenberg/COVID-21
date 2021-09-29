package org.um.nine.domain;

public enum RoundState {
    ACTIONS,
    DRAW,
    INFECT;

    public RoundState nextState(RoundState current) {
        if (current == null) {
            return RoundState.ACTIONS;
        }

        if (current.equals(RoundState.ACTIONS)) {
            return RoundState.DRAW;
        }

        if (current.equals(RoundState.DRAW)) {
            return RoundState.INFECT;
        }

        return null;
    }
}
