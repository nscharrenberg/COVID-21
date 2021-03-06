package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.contracts.repositories.IEpidemicRepository;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

public class EpidemicRepository implements IEpidemicRepository {

    @Override
    public EpidemicRepository clone() {
        try {
            return (EpidemicRepository) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void action(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        increase(state);
        infect(state);
        intensify(state);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EpidemicRepository;
    }
}
