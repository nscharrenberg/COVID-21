package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.contracts.repositories.IEpidemicRepository;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

public class EpidemicRepository implements IEpidemicRepository {

    @Override
    public void action(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        increase(state);
        infect(state);
        intensify(state);
    }

}
