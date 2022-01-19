package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.RPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.GameOverException;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.game.Settings.*;

public record Mutator() implements IReportable {
    //public static final int N_EVALUATION_SIMULATIONS = 5;
    public static final double INITIAL_MUTATION_RATE = 1d, FINAL_MUTATION_RATE = 0.5;
    public static final int N_MUTATIONS = 200;
    public static int successfulMutations = 0;

    public static double map(double value, double min1, double max1, double min2, double max2) {
        return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
    }


    public Individual mutateIndividual(IState initialState, Individual individual, double mutationRate) throws GameOverException {

        IState mutationState = initialState.clone();
        boolean atLeastOneMutated = false;
        MacroAction[] newGenome = null;

        for (int i = 0; i < ROLLING_HORIZON; i++) {
            double mutationChance = RANDOM_PROVIDER.nextDouble();
            if (mutationChance < mutationRate) {
                atLeastOneMutated = true;
                newGenome = mutateGene2(mutationState, individual, i);
            }
        }

        if (!atLeastOneMutated) {
            int mutationIndex = RANDOM_PROVIDER.nextInt(ROLLING_HORIZON);
            newGenome = mutateGene2(mutationState, individual, mutationIndex);
        }

        return new Individual(newGenome);
    }

    private MacroAction[] mutateGene(IState individualMutationState, Individual individual, int mutationIndex) throws GameOverException {
        List<PlayerCard> hand = new ArrayList<>(individualMutationState.getPlayerRepository().getCurrentPlayer().getHand());
        IState mutationState = individualMutationState.clone();
        Player p = mutationState.getPlayerRepository().getCurrentPlayer();

        MacroAction[] newGene = new MacroAction[ROLLING_HORIZON];
        for (int i = 0; i < ROLLING_HORIZON; i++) {
            MacroAction macroIndex = individual.genome()[i].executableNow(mutationState);  // copy the existing macro
            mutationState.getPlayerRepository().setCurrentPlayer(p);

            if (i == mutationIndex)
                macroIndex = HPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();
            else if (i > mutationIndex)
                macroIndex = RPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();

            try {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(mutationState, macroIndex, true);
            } catch (GameOverException e) {
                //System.err.println(e.getMessage() + " : " + IReportable.getDescription());
                //we want to break the mutation at the upper level, so keep throwing the exception just if it's game over
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                newGene[i] = macroIndex;
            }
        }

        if (!individualMutationState.getPlayerRepository().getCurrentPlayer().getHand().equals(hand)) {
            throw new IllegalStateException();
        }
        return newGene;

    }

    private MacroAction[] mutateGene2(IState geneMutation, Individual individual, int mutationIndex) {
        IState mutationState = geneMutation.clone();
        Player p = mutationState.getPlayerRepository().getCurrentPlayer();
        MacroAction[] newGene = new MacroAction[ROLLING_HORIZON];
        for (int i = 0; i < ROLLING_HORIZON; i++) {
            MacroAction macroIndex;
            mutationState.getPlayerRepository().setCurrentPlayer(p);
            if (i < mutationIndex) macroIndex = individual.genome()[i];
            else if (i == mutationIndex)
                macroIndex = RPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();
            else macroIndex = HPAMacroActionsFactory.init(mutationState, p.getCity(), p).getNextMacroAction();
            try {
                macroIndex = macroIndex.executableNow(mutationState);
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(mutationState, macroIndex, true);
            } catch (GameOverException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            newGene[i] = macroIndex;
        }
        return newGene;
    }

}
