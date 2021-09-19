package org.um.nine;

import com.google.inject.Inject;
import com.jme3.app.SimpleApplication;
import org.um.nine.contracts.repositories.IGameRepository;


public class Game extends SimpleApplication {

    @Inject
    private IGameRepository gameRepository;

    @Override
    public void simpleInitApp() {
        gameRepository.create();
    }

    @Override
    public void simpleUpdate(float tpf) {
        gameRepository.update();
    }
}
