package org.um.nine;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import org.um.nine.contracts.repositories.IGameRepository;

public class Game extends SimpleApplication {
    private final IGameRepository gameRepository = Main.injector.getInstance(IGameRepository.class);

    public Game(AppState... states) {
        super(states);
    }

    @Override
    public void simpleInitApp() {
        gameRepository.create();
    }

    @Override
    public void simpleUpdate(float tpf) {
        gameRepository.update();
    }
}
