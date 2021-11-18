package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.contracts.repositories.IEpidemicRepository;

public class EpidemicRepository implements IEpidemicRepository {

    @Override
    public void action() {
        increase();
        infect();
        intensify();
    }
}
