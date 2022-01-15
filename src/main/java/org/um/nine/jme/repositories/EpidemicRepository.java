package org.um.nine.jme.repositories;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

public class EpidemicRepository {

    public IState getState() {
        return GameStateFactory.getInitialState();
    }

    public void action(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        GameStateFactory.getInitialState().getEpidemicRepository().action(state);
    }

}
