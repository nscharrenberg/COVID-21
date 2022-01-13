package org.um.nine.headless.agents.rhea.core;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.Player;

import static org.um.nine.headless.game.Settings.*;

public record Mutator() implements IReportable {
    //public static final int N_EVALUATION_SIMULATIONS = 5;
    public static final double INITIAL_MUTATION_RATE = 1d, FINAL_MUTATION_RATE = 0.5;
    public static final int N_MUTATIONS = 10;
    public static int successfulMutations = 0;

    public static double map(double value, double min1, double max1, double min2, double max2) {
        return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
    }

    public void mutateIndividual(IState initialState, Individual individual, double mutationRate) throws Exception {
        boolean atLeastOneMutated = false;
        String mutationIndexPath = getPath();


        for (int i = 0; i < ROLLING_HORIZON; i++) {
            if (LOG) setPath(mutationIndexPath + "/gene-" + i);

            IState mutationState = initialState.getClonedState();
            double mutationChance = RANDOM_PROVIDER.nextDouble();

            if (mutationChance < mutationRate) {
                atLeastOneMutated = true;
                mutateGene(mutationState, individual, i);
            }
        }

        if (!atLeastOneMutated) {
            //mutate at least one
            int mutationIndex = RANDOM_PROVIDER.nextInt(ROLLING_HORIZON);
            if (LOG) setPath(mutationIndexPath + "/gene-" + mutationIndex);

            mutateGene(initialState.getClonedState(), individual, mutationIndex);
        }

        if (LOG) setPath(mutationIndexPath);
    }

    private void mutateGene(IState initialState, Individual individual, int mutationIndex) throws Exception {
        Player p = initialState.getPlayerRepository().getCurrentPlayer();
        String mutatedGeneIndexPath = getPath();

        for (int i = 0; i < ROLLING_HORIZON; i++) {
            ROUND_INDEX = i;
            MacroAction macroIndex = individual.genome()[i];
            if (LOG) {
                setPath(mutatedGeneIndexPath + "/round-" + ROUND_INDEX + "-" + p.getCity().getName() + ".txt");
                append("Given macro action " + macroIndex.toString());
            }
            boolean mutated = i >= mutationIndex;
            if (i == mutationIndex) {
                individual.genome()[i] = (macroIndex = HPAMacroActionsFactory.init(initialState, p.getCity(), p).getNextMacroAction());
            } else if (i > mutationIndex) {
                individual.genome()[i] = (macroIndex = RPAMacroActionsFactory.init(initialState, p.getCity(), p).getNextMacroAction());
            }
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initialState, macroIndex, true);
            initialState.getPlayerRepository().setCurrentPlayer(p);
            if (LOG) {
                if (mutated)
                    append("Mutated by " + (i == mutationIndex ? "HPA" : "RPA") + " macro action " + individual.genome()[i]);
                else append("Given macro action is being kept");
                report();
            }
        }
    }

}
