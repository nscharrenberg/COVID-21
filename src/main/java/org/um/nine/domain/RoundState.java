package org.um.nine.domain;

public enum RoundState {
    ACTION,
    DRAW,
    INFECT;

    static int actionsLeft = 4;
    static int drawLeft = 2;
    private static int infection_rate = 2;
    private static int infectionLeft = infection_rate;


    /**
     * @return null if the turn has ended otherwise the RoundState of the turn
     */
    public static RoundState nextState(RoundState currentState){
        if (currentState == null)
            return ACTION;
        else if (currentState == ACTION){
            actionsLeft--;
            if (actionsLeft == 0) {
                actionsLeft = 4;
                return DRAW;
            } else return ACTION;
        } else if (currentState == DRAW){
            drawLeft--;
            if (drawLeft == 0){
                drawLeft = 2;
                return INFECT;
            } else return DRAW;
        } else if (currentState == INFECT){
            infectionLeft--;
            if (infectionLeft == 0){
                infectionLeft = infection_rate;
                return null;
            } else return INFECT;
        }
        throw new IllegalStateException();
    }

    public static void increaseInfectionRate(int inf_rate){
        infection_rate = inf_rate;
    }

}
