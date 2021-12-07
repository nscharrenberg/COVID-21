package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.RheaExperiment;

import static org.um.nine.headless.game.Settings.HEADLESS;

public class ExperimentRunner {

    public static void main(String[] args) {
        if (HEADLESS) {
            ExperimentalGame game = new ExperimentalGame();
            game.start();
            game.getCurrentState().getClonedState();
            RheaExperiment experiment = new RheaExperiment(game);
            experiment.runExperiment();
        }
    }
}
