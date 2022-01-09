package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.state.IState;

import static org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory.init;
import static org.um.nine.headless.game.Settings.*;

public record Mutator(ExperimentalGame game) {

    public static final double INITIAL_MUTATION_RATE = 1d, FINAL_MUTATION_RATE = 0.5;
    public static final int N_EVALUATION_SIMULATIONS = 5;
    public static final int N_MUTATIONS = 100;
    public static int successfulMutations = 0;


    public static double map(double value, double min1, double max1, double min2, double max2) {
        return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
    }

    public void mutateIndividual(IState initialState, Individual individual, double mutationRate) {

        boolean atLeastOneMutated = false;

        for (int i = 0; i < ROLLING_HORIZON; i++) {
            double mutationChance = RANDOM_PROVIDER.nextDouble();
            if (mutationChance < mutationRate) {
                atLeastOneMutated = true;
                mutateGene(initialState, individual, i);
            }
        }

        if (!atLeastOneMutated) {
            //mutate at least one
            int mutationIndex = RANDOM_PROVIDER.nextInt(ROLLING_HORIZON);
            mutateGene(initialState, individual, mutationIndex);
        }

    }

    private void mutateGene(IState initialState, Individual individual, int mutationIndex) {
        for (int i = 0; i < ROLLING_HORIZON; i++) {
            MacroAction macroIndex = individual.genome()[i];
            if (i < mutationIndex) {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            } else if (i == mutationIndex) {
                individual.genome()[i] = (macroIndex = init(initialState, initialState.getPlayerRepository().getCurrentPlayer().getCity(), initialState.getPlayerRepository().getCurrentPlayer()).getNextMacroAction());
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            } else {
                individual.genome()[i] = (macroIndex = RPAMacroActionsFactory.getNextMacroAction(initialState));
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            }
        }
    }

}
