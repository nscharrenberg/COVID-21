package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.contracts.repositories.IEpidemicRepository;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

public class EpidemicRepository implements IEpidemicRepository {

    private IState state;

    @Override
    public IEpidemicRepository setState(IState state) {
        this.state = state;
        return this;
    }

    @Override
    public IState getState() {
        return this.state;
    }

    @Override
    public void action() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        increase();
        infect();
        intensify();
    }

}
