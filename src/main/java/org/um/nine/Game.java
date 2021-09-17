package org.um.nine;

import com.jme3.app.SimpleApplication;
import org.um.nine.contracts.repositories.IGameRepository;

import javax.inject.Inject;

public class Game extends SimpleApplication {
    private IGameRepository gameRepository;

    @Override
    public void simpleInitApp() {
        gameRepository.create();
    }

    @Override
    public void simpleUpdate(float tpf) {
        gameRepository.update();
    }

    @Inject
    public void setGameRepository(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
}
