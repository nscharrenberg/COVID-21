package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;

import java.util.Arrays;

import static org.um.nine.headless.agents.rhea.core.Mutator.*;
import static org.um.nine.headless.game.Settings.*;

public final record Individual(MacroAction[] genome) implements IAgent {

    public double averageEvaluationIndividual() {
        double avg = 0;
        IState state = DEFAULT_RUNNING_GAME.getInitialState();
        for (MacroAction m : genome) {
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, m, true);
            avg += BEST_HEURISTIC.evaluateState(state);
        }
        return avg / genome.length;
    }

    public double[] evaluationIndividual() {
        double[] eval = new double[genome.length];
        IState state = DEFAULT_RUNNING_GAME.getInitialState();
        for (int i = 0; i < genome().length; i++) {
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, genome[i], true);
            eval[i] = BEST_HEURISTIC.evaluateState(state);
        }
        return eval;
    }

    public boolean betterThan(Individual other) {
        double[] eval1 = this.evaluationIndividual();
        double[] eval2 = other.evaluationIndividual();
        // all better or false
        for (int i = 0; i < genome.length; i++) {
            if (eval2[i] > eval1[i]) return false;
        }
        return true;
    }

    public Individual generateChild() {
        return new Individual(Arrays.stream(genome()).map(MacroAction::getClone).toArray(MacroAction[]::new));
    }


    public MacroAction getNextMacroAction(IState state) {
        IState mutationState = state.getClonedState();
        Individual ancestor = this;


        HPAMacroActionsFactory.initIndividualGene(mutationState, ancestor);
        successfulMutations = 0;

        for (int i = 0; i < N_MUTATIONS; i++) {
            double mutationRate = map(
                    ((double) (i + 1)) / N_MUTATIONS,
                    0,
                    1,
                    INITIAL_MUTATION_RATE,
                    FINAL_MUTATION_RATE
            );

            Individual child = ancestor.generateChild();
            DEFAULT_MUTATOR.mutateIndividual(state, child, mutationRate);

            if (child.betterThan(ancestor)) {  //all macro actions are better
                successfulMutations++;
                ancestor = child.generateChild(); //cloned
            }
        }

        return ancestor.genome()[0];
    }

}
