package org.um.nine.headless.agents.rhea;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import java.util.Random;

public class Individual implements Comparable<Individual>{

    protected final int[] actions; // actions in individual. length of individual = actions.length
    private final int nAllowedActions; // number of legal actions
    protected double fitness;
    private final Random gen;

    protected Individual(int l, int nActions, Random dna) {
        actions = new int[l];
        for (int i = 0; i < l; i++) {
            actions[i] = dna.nextInt(nActions);
        }
        this.nAllowedActions = nActions;
        this.gen = dna;
    }

    public void setActions (int[] a) {
        if (this.actions.length!= a.length) throw new IllegalArgumentException("Number of allowed actions doesn't match.");
        System.arraycopy(a, 0, actions, 0, a.length);
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns new individual
     * @param m - number of genes to mutate
     * @return - new individual, mutated from this
     */
    protected Individual mutate(int m) {
        Individual n = this.copy();
        n.setActions(actions);
        int count = 0;
        if (nAllowedActions > 1) { // make sure you can actually mutate
            while (count < m) {
                // random mutation of one action at random index
                n.actions[gen.nextInt(n.actions.length)] = gen.nextInt(nAllowedActions);
                count++;
            }
        }
        return n;
    }

    /**
     * Modifies individual
     * @param crossoverType - type of crossover
     */
    public void crossover (Individual p1, Individual p2, int crossoverType) {
        if (crossoverType == RheaAgent.POINT1_CROSS) {
            // 1-point
            int p = gen.nextInt(actions.length - 3) + 1;
            for ( int i = 0; i < actions.length; i++) {
                if (i < p)
                    actions[i] = p1.actions[i];
                else
                    actions[i] = p2.actions[i];
            }

        } else if (crossoverType == RheaAgent.UNIFORM_CROSS) {
            // uniform
            for (int i = 0; i < actions.length; i++) {
                if (gen.nextFloat() >= 0.5)
                    actions[i] = p1.actions[i];
                else
                    actions[i] = p2.actions[i];
            }
        }
    }

    @Override
    public int compareTo(@NotNull Individual o) {
        return Double.compare(o.fitness, this.fitness);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Individual b) || this.actions.length!= b.actions.length) return false;
        for (int i = 0; i < actions.length; i++) {
            if (this.actions[i] != b.actions[i]) return false;
        }
        return true;
    }

    public Individual copy () {
        Individual a = new Individual(this.actions.length, this.nAllowedActions, this.gen);
        a.setFitness(this.fitness);
        a.setActions(this.actions);
        return a;
    }
}