package org.um.nine;

import com.jme3.app.SimpleApplication;
import org.um.nine.contracts.repositories.IGameRepository;

public class Game extends SimpleApplication {
    private final IGameRepository gameRepository = Main.injector.getInstance(IGameRepository.class);

    @Override
    public void simpleInitApp() {
        gameRepository.create();

    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        gameRepository.update();
    }
}
