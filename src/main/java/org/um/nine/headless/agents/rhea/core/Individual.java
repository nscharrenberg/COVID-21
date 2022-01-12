package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.Arrays;

import static org.um.nine.headless.agents.rhea.core.Mutator.*;
import static org.um.nine.headless.game.Settings.*;

public final record Individual(MacroAction[] genome) implements IAgent, IReportable {

    public double[] evaluateIndividual(IState state) {
        double[] eval = new double[genome.length];
        IState evaluationState = state.getClonedState();
        for (int i = 0; i < genome().length; i++) {
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(evaluationState, genome[i], true);
            eval[i] = BEST_HEURISTIC.evaluateState(evaluationState);
        }
        return eval;
    }

    public boolean betterThan(Individual other, IState state) {
        double[] eval1 = this.evaluateIndividual(state);
        double[] eval2 = other.evaluateIndividual(state);
        // all better or false
        for (int i = 0; i < genome.length; i++) {
            if (eval2[i] > eval1[i]) return false;
        }
        return true;
    }

    public Individual initGenome(IState state) {
        IState initState = state.getClonedState();
        Player player = initState.getPlayerRepository().getCurrentPlayer();
        String gamePath = getPath();
        String initGenomePath = gamePath + "/" + player.toString() + "/genome-init";
        if (LOG) setPath(initGenomePath);

        for (int i = 0; i < this.genome().length; i++) {
            ROUND_INDEX = i;

            City currentCity = player.getCity();
            if (LOG) setPath(initGenomePath + "/round-" + ROUND_INDEX + "-" + currentCity.getName() + ".txt");
            MacroAction nextMacro = HPAMacroActionsFactory.init(initState, currentCity, player).getNextMacroAction();

            if (LOG) append("\nChosen macro : " + nextMacro);

            this.genome()[i] = nextMacro;
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initState, nextMacro, true);
            initState.getPlayerRepository().setCurrentPlayer(player); //trick the game logic here to allow fault turn
            report();
        }
        ROUND_INDEX = 0;
        if (LOG) setPath(gamePath);
        return this;
    }

    public Individual generateChild() {
        return new Individual(Arrays.stream(genome()).map(MacroAction::getClone).toArray(MacroAction[]::new));
    }

    public MacroAction getNextMacroAction(IState initialGameState) {
        Individual ancestor = this;
        successfulMutations = 0;
        String gamePath = getPath();


        for (int i = 0; i < N_MUTATIONS; i++) {
            IState mutationState = initialGameState.getClonedState();

            String mutationPath = getPath() + "/" + mutationState.getPlayerRepository().getCurrentPlayer().toString() + "/mutation-" + i;
            if (LOG) setPath(mutationPath);

            double mutationRate = map(
                    ((double) (i + 1)) / N_MUTATIONS,
                    0,
                    1,
                    INITIAL_MUTATION_RATE,
                    FINAL_MUTATION_RATE
            );

            Individual child = ancestor.generateChild();

            DEFAULT_MUTATOR.mutateIndividual(mutationState, child, mutationRate);

            if (child.betterThan(ancestor, mutationState)) {  //all macro actions are better
                successfulMutations++;
                ancestor = child.generateChild(); //cloned
            }
        }
        if (LOG) setPath(gamePath);
        return ancestor.genome()[0];
    }

}