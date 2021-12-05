package org.um.nine.headless;

import org.um.nine.headless.agents.rhea.RheaExperiment;
import org.um.nine.headless.game.Game;

import static org.um.nine.headless.game.Settings.HEADLESS;

public class Main {
    public static void main(String[] args) {
        if(HEADLESS){
            Game game = new Game();
            game.start();
            game.getCurrentState().getClonedState();
            RheaExperiment experiment = new RheaExperiment(game);
            experiment.runExperiment();
        }
    }
}
