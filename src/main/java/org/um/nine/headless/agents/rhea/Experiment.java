package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.Report;
import org.um.nine.headless.game.Game;

import java.util.List;

public interface Experiment {
    List<Game> games();
    Report report();
    void runExperiment();

}
