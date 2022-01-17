package org.um.nine.headless.agents.rhea.core;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.exceptions.GameOverException;

import static org.um.nine.headless.game.Settings.*;

public record Mutator() implements IReportable {
    //public static final int N_EVALUATION_SIMULATIONS = 5;
    public static final double INITIAL_MUTATION_RATE = 1d, FINAL_MUTATION_RATE = 0.5;
    public static final int N_MUTATIONS = 3;
    public static int successfulMutations = 0;

    public static double map(double value, double min1, double max1, double min2, double max2) {
        return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
    }

    public void mutateIndividual(IState initialState, Individual individual, double mutationRate) throws GameOverException {
        IState mutationState = initialState.clone();
        boolean atLeastOneMutated = false;

        for (int i = 0; i < ROLLING_HORIZON; i++) {
            double mutationChance = RANDOM_PROVIDER.nextDouble();
            if (mutationChance < mutationRate) {
                atLeastOneMutated = true;
                mutateGene(mutationState, individual, i);
            }
        }

        if (!atLeastOneMutated) {
            //mutate at least one
            int mutationIndex = RANDOM_PROVIDER.nextInt(ROLLING_HORIZON);
            mutateGene(mutationState, individual, mutationIndex);
        }
    }

    private void mutateGene(IState individualMutationState, Individual individual, int mutationIndex) throws GameOverException {
        IState mutationState = individualMutationState.clone();
        Player p = mutationState.getPlayerRepository().getCurrentPlayer();


        //TODO : if executing previous macro throws exception replace with skip

        //TODO: make method applicableOnState now in macro actions where return the okay ones

        //public MacroAction applicableNow(IState stateNow, Macro toApply)
        // use it here and in genome
        // check also if game lost before applying it


        for (int i = 0; i < ROLLING_HORIZON; i++) {
            ROUND_INDEX = i;
            MacroAction macroIndex = individual.genome()[i];
            mutationState.getPlayerRepository().setCurrentPlayer(p);
            if (i == mutationIndex || macroIndex == null) {
                macroIndex = HPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();
            } else if (i > mutationIndex) {
                macroIndex = RPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();
            }
            individual.genome()[i] = macroIndex;
            try {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(mutationState, macroIndex, true);
            } catch (GameOverException e) {
                System.err.println(e.getMessage() + " : " + IReportable.getDescription());
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
