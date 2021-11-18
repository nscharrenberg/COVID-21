package org.um.nine.agents.Rhea;

import org.um.nine.domain.ActionType;
import org.um.nine.utils.versioning.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class RheaAgent {
    private static final int MAX_ITERATIONS = 500;
    private final int SIMULATION_DEPTH = 10;
    static final int POINT1_CROSS = 0;
    static final int UNIFORM_CROSS = 1;

    private Individual[] population, nextPop;
    private int NUM_INDIVIDUALS;
    private int N_ACTIONS;
    private HashMap<Integer, ActionType>action_mapping;
    private final Random randomGenerator;
    private int nIterates = 0;
    private boolean keepIterating = true;
    private final StateHeuristic heuristic;
    private static int TOURNAMENT_SIZE = 2;


    public RheaAgent(StateObservation stateObs){
        this.randomGenerator = new Random();
        this.heuristic = state -> 0;
    }

    private static int compare(Individual o1, Individual o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return 1;
        if (o2 == null) return -1;
        return o1.compareTo(o2);
    }

    private void runIteration(StateObservation stateObs) {
        int ELITISM = 1;
        int MUTATION = 1;
        if (NUM_INDIVIDUALS > 1) {
            for (int i = ELITISM; i < NUM_INDIVIDUALS; i++) {
                    Individual newind;

                    newind = crossover();
                    newind = newind.mutate(MUTATION);
                    // evaluate new individual, insert into population
                    add_individual(newind, nextPop, i, stateObs);
            }

            Arrays.sort(nextPop, RheaAgent::compare);

        } else if (NUM_INDIVIDUALS == 1){
            Individual newIndividual = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator).mutate(MUTATION);
            evaluate(newIndividual, heuristic, stateObs);
            if (newIndividual.fitness > population[0].fitness)
                nextPop[0] = newIndividual;
        }

        population = nextPop.clone();
        nIterates++;
        if (nIterates >MAX_ITERATIONS) keepIterating = false;

    }

    public ActionType act(StateObservation stateObs) {
        nIterates = 0;
        NUM_INDIVIDUALS = 0;
        keepIterating = true;
        // INITIALISE POPULATION
        init_pop(stateObs);
        // RUN EVOLUTION
        while (keepIterating) runIteration(stateObs);

        // RETURN ACTION
        return get_best_action(population);
    }

    /**
     * Evaluates an individual by rolling the current state with the actions in the individual
     * and returning the value of the resulting state; random action chosen for the opponent
     * @param individual - individual to be valued
     * @param heuristic - heuristic to be used for state evaluation
     * @param state - current state, root of rollouts
     * @return - value of last state reached
     */
    private double evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {
        StateObservation st = state.copy();
        for (int i = 0; i < SIMULATION_DEPTH; i++) {
            if (! st.isGameOver())
                st.advance(action_mapping.get(individual.actions[i]));
        }
        individual.fitness = heuristic.evaluateState((State)st);
        return individual.fitness;
    }

    private Individual crossover(){
        Individual individual = null;
        if (NUM_INDIVIDUALS > 1) {
            individual = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
            Individual[] tournament = new Individual[TOURNAMENT_SIZE];
            ArrayList<Individual> list = new ArrayList<>(Arrays.asList(population));
            //Select a number of random distinct individuals for tournament and sort them based on value
            for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                int index = randomGenerator.nextInt(list.size());
                tournament[i] = list.get(index);
                list.remove(index);
            }
            Arrays.sort(tournament, RheaAgent::compare);

            //get best individuals in tournament as parents
            if (TOURNAMENT_SIZE >= 2) {
                individual.crossover(tournament[0], tournament[1], UNIFORM_CROSS);
            } else {
                System.out.println("WARNING: Number of parents must be LESS than tournament size.");
            }
        }
        return individual;
    }

    /**
     * Insert a new individual into the population at the specified position by replacing the old one.
     * @param ind - individual to be inserted into population
     * @param pop - population
     * @param idx - position where individual should be inserted
     * @param stateObs - current game state
     */
    private void add_individual(Individual ind, Individual[] pop, int idx, StateObservation stateObs) {
        evaluate(ind, heuristic, stateObs);
        pop[idx] = ind.copy();
    }


    /**
     * Initialize population
     * @param stateObs - current game state
     */
    private void init_pop(StateObservation stateObs) {


        N_ACTIONS = stateObs.getAvailableActions().size() + 1;
        action_mapping = new HashMap<>();
        int k = 0;
        for (ActionType action : stateObs.getAvailableActions()) {
            action_mapping.put(k, action);
            k++;
        }
        action_mapping.put(k, ActionType.NO_ACTION);

        int POPULATION_SIZE = 10;
        population = new Individual[POPULATION_SIZE];
        nextPop = new Individual[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (i == 0 || keepIterating) {
                population[i] = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
                evaluate(population[i], heuristic, stateObs);
                NUM_INDIVIDUALS = i+1;
            } else {break;}
        }

        if (NUM_INDIVIDUALS > 1) {
            Arrays.sort(population, RheaAgent::compare);
        }
        for (int i = 0; i < NUM_INDIVIDUALS; i++) {
            if (population[i] != null)
                nextPop[i] = population[i].copy();
        }

    }
    /**
     * @param pop - last population obtained after evolution
     * @return - first action of best individual in the population (found at index 0)
     */
    private ActionType get_best_action(Individual[] pop) {
        int bestAction = pop[0].actions[0];
        return action_mapping.get(bestAction);
    }


}
