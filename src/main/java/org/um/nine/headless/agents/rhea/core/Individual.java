package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.exceptions.GameOverException;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import static org.um.nine.headless.agents.rhea.core.Mutator.*;
import static org.um.nine.headless.agents.rhea.macro.MacroAction.skipMacroAction;
import static org.um.nine.headless.game.Settings.*;

public final record Individual(MacroAction[] genome) implements IAgent, IReportable {


    @Override
    protected Individual clone() {
        return new Individual(Arrays.stream(genome()).map(macro -> macro == null ? null : macro.clone()).toArray(MacroAction[]::new));
    }

    public double[] evaluateIndividual(IState state) throws Exception {
        double[] eval = new double[genome.length];
        IState evaluationState = state.clone();
        Player player = evaluationState.getPlayerRepository().getCurrentPlayer();

        for (int i = 0; i < genome().length; i++) {
            evaluationState.getPlayerRepository().setCurrentPlayer(player);
            if (genome[i] == null) genome()[i] = skipMacroAction(4, player.getCity());
            MacroAction executable = genome()[i].executableNow(evaluationState);
            try {
                DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(evaluationState, executable, true);
            } catch (GameOverException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                eval[i] = BEST_HEURISTIC.evaluateState(evaluationState);

            }
        }
        return eval;
    }

    public boolean betterThan(Individual other, IState state, EvaluationType evaluationType) throws Exception {
        IState evaluationState = state.clone();
        double[] thisEvaluation = this.evaluateIndividual(evaluationState);
        double[] thatEvaluation = other.evaluateIndividual(evaluationState);
        // all better or false

        BooleanSupplier evaluator = null;
        switch (evaluationType) {
            case ALL_BETTER -> evaluator = () -> {
                for (int i = 0; i < this.genome().length; i++) if (thatEvaluation[i] >= thisEvaluation[i]) return false;
                return true;
            };

            case ONE_BETTER -> evaluator = () -> {
                for (int i = 0; i < this.genome().length; i++) if (thisEvaluation[i] < thatEvaluation[i]) return true;
                return false;
            };

            case AVERAGE_BETTER -> evaluator = () -> {
                double avgThis = 0, avgThat = 0;
                for (int i = 0; i < this.genome().length; i++) {
                    avgThis += thisEvaluation[i];
                    avgThat += thatEvaluation[i];
                }
                avgThis /= thisEvaluation.length;
                avgThat /= thatEvaluation.length;
                return avgThis > avgThat;
            };
        }

        return evaluator.getAsBoolean();
    }

    public enum EvaluationType {
        ALL_BETTER,
        ONE_BETTER,
        AVERAGE_BETTER
    }


    public Individual initGenome(IState state) {
        IState initState = state.clone();
        Player player = initState.getPlayerRepository().getCurrentPlayer();
        String playerPath = this.getPath();
        for (int i = 0; i < this.genome().length; i++) {
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

        this.setPath(playerPath);
        this.logGenome(this.genome(), "/genome-init.txt");
        return this;
    }

    public Individual initSkipActionGenome(IState initState) {
        Player player = initState.getPlayerRepository().getCurrentPlayer();
        String playerPath = this.getPath();
        MacroAction[] skipMacros =
                IntStream.range(0, ROLLING_HORIZON).
                        mapToObj(i -> skipMacroAction(4, player.getCity()
                        )).
                        toArray(MacroAction[]::new);

        this.setPath(playerPath);
        Individual allSkip = new Individual(skipMacros);
        this.logGenome(allSkip.genome(), "/genome-init.txt");
        return allSkip;
    }

    public MacroAction getNextMacroAction(IState initialGameState) {

        // first we initialise the individual within its horizon
        // this will be a set of (#ROLLING_HORIZON) macro actions to be applied in a row


        //init genome until rolling horizon
        Individual ancestor = this.initGenome(initialGameState);
        //Individual ancestor = this.initSkipActionGenome(initialGameState);

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

            try {

                // mutate the individual starting from the initial state
                Individual child = ancestor.clone();
                DEFAULT_MUTATOR.mutateIndividual(initialGameState, ancestor, mutationRate);
                //Individual child = DEFAULT_MUTATOR.mutateIndividual2(initialGameState,ancestor,mutationRate);

                // use heuristic to evaluate the (#ROLLING HORIZON) states produced by all macros being applied
                if (child.betterThan(ancestor, initialGameState, EvaluationType.ONE_BETTER)) {  //all macro actions are better
                    successfulMutations++;
                    ancestor = child.clone();   // take macro actions from the better scoring mutated individual
                    this.logGenome(ancestor.genome(), "/mutation-" + i + ".txt");
                }

            } catch (GameOverException gameOver) {
                //System.err.println(gameOver.getMessage() + " :: " + IReportable.REPORT_PATH[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Successful mutations : " + successfulMutations);
        setPath(playerPath);
        return ancestor.genome()[0];
    }

}