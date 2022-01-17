package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.exceptions.GameOverException;

import java.util.Arrays;

import static org.um.nine.headless.agents.rhea.core.Mutator.*;
import static org.um.nine.headless.game.Settings.*;

public final record Individual(MacroAction[] genome) implements IAgent, IReportable {


    @Override
    protected Individual clone() {
        MacroAction[] clonedGene = Arrays.stream(this.genome()).map(MacroAction::clone).toArray(MacroAction[]::new);
        return new Individual(clonedGene);
    }

    public double[] evaluateIndividual(IState state) throws Exception {
        double[] eval = new double[genome.length];
        IState evaluationState = state.clone();
        Player player = evaluationState.getPlayerRepository().getCurrentPlayer();

        for (int i = 0; i < genome().length; i++) {
            evaluationState.getPlayerRepository().setCurrentPlayer(player);
            //if (genome[i] == null) genome[i] = skipMacroAction(4, player.getCity());

            try {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(evaluationState, genome[i], true);
                eval[i] = BEST_HEURISTIC.evaluateState(evaluationState);
            } catch (GameOverException gameOver) {
                eval[i] = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return eval;
    }

    public boolean betterThan(Individual other, IState state) throws Exception {
        double[] eval1 = this.evaluateIndividual(state);
        double[] eval2 = other.evaluateIndividual(state);
        // all better or false
        for (int i = 0; i < genome.length; i++) {
            if (eval2[i] > eval1[i]) return false;
        }
        return true;
    }

    public Individual initGenome(IState state) {
        IState initState = state.clone();
        Player player = initState.getPlayerRepository().getCurrentPlayer();
        String playerPath = this.getPath();

        for (int i = 0; i < this.genome().length; i++) {
            ROUND_INDEX = i;
            City currentCity = player.getCity();
            MacroAction nextMacro = HPAMacroActionsFactory.init(initState, currentCity, player).getNextMacroAction();

            this.genome()[i] = nextMacro = nextMacro.executableNow(initState);

            try {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(initState, nextMacro, true);
            } catch (GameOverException gameOver) {
                //System.err.println(gameOver.getMessage() + " : " + IReportable.getDescription());

            } catch (Exception e) {
                e.printStackTrace();
            }
            initState.getPlayerRepository().setCurrentPlayer(player); //trick the game logic here to allow fault turn
        }
        ROUND_INDEX = 0;

        if (LOG) {
            this.setPath(playerPath);
            this.logGenome(this.genome(), "/genome-init.txt");
        }
        return this;
    }

    public Individual generateChild() {
        return new Individual(Arrays.stream(genome()).map(macro -> macro == null ? null : macro.clone()).toArray(MacroAction[]::new));
    }

    public MacroAction getNextMacroAction(IState initialGameState) {

        // first we initialise the individual within its horizon
        // this will be a set of (#ROLLING_HORIZON) macro actions to be applied in a row
        Individual ancestor = this.initGenome(initialGameState);


        successfulMutations = 0;
        String playerPath = getPath();


        // for a fixed amount of iterations
        for (int i = 0; i < N_MUTATIONS; i++) {
            double mutationRate = map(
                    ((double) (i + 1)) / N_MUTATIONS,
                    0,
                    1,
                    INITIAL_MUTATION_RATE,
                    FINAL_MUTATION_RATE
            );

            // copy the genome for the mutation
            Individual child = ancestor.generateChild();

            try {

                // mutate the individual starting from the initial state
                DEFAULT_MUTATOR.mutateIndividual(initialGameState, child, mutationRate);
                // use heuristic to evaluate the (#ROLLING HORIZON) states produced by all macros being applied
                if (child.betterThan(ancestor, initialGameState)) {  //all macro actions are better
                    successfulMutations++;
                    ancestor = child.clone(); //clone the current individual
                    this.logGenome(ancestor.genome(), "/mutation-" + i + ".txt");
                }

            } catch (GameOverException gameOver) {
                //System.err.println(gameOver.getMessage() + " :: " + IReportable.REPORT_PATH[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (LOG) setPath(playerPath);
        return ancestor.genome()[0];
    }

}