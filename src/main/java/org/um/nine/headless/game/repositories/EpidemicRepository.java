package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.contracts.repositories.IEpidemicRepository;

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
    public void action() {
        increase();
        infect();
        intensify();
    }

}
