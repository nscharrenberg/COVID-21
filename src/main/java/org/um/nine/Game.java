package org.um.nine;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import org.um.nine.services.GameService;

public class Game extends SimpleApplication {
    private final GameService gameService = Main.injector.getInstance(GameService.class);

    public Game(AppState... states) {
        super(states);
    }

    @Override
    public void simpleInitApp() {
        gameService.create();
    }

    @Override
    public void simpleUpdate(float tpf) {
        gameService.update();
    }
}
