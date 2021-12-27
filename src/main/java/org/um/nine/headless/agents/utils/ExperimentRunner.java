package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.rhea.MacroActionsExecutor;

import static org.um.nine.headless.game.Settings.HEADLESS;

public class ExperimentRunner {

    public static void main(String[] args) {


        if (HEADLESS) {
            ExperimentalGame game = new ExperimentalGame();
            MacroActionsExecutor experiment = new MacroActionsExecutor(game);
            experiment.runExperimentalGame();

        }
    }
}
