package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.Player;

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
        Player p = initialState.getPlayerRepository().getCurrentPlayer();
        for (int i = 0; i < ROLLING_HORIZON; i++) {
            MacroAction macroIndex = individual.genome()[i];
            if (i < mutationIndex) {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            } else if (i == mutationIndex) {
                individual.genome()[i] = (macroIndex = HPAMacroActionsFactory.init(initialState, p.getCity(), p).getNextMacroAction());
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            } else {
                individual.genome()[i] = (macroIndex = RPAMacroActionsFactory.init(initialState, p.getCity(), p).getNextMacroAction());
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            }
            initialState.getPlayerRepository().setCurrentPlayer(p);
        }
    }

}
