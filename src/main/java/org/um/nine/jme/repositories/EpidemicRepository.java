package org.um.nine.jme.repositories;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

public class EpidemicRepository {

    public IState getState() {
        return GameStateFactory.getInitialState().getEpidemicRepository().getState();
    }

    public void action() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        GameStateFactory.getInitialState().getEpidemicRepository().action();
    }

}
