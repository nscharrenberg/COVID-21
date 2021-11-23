package org.um.nine.jme;

import com.jme3.app.SimpleApplication;
import org.um.nine.jme.repositories.GameRepository;

public class JmeGame extends SimpleApplication {
    @Override
    public void simpleInitApp() {
        JmeMain.getGameRepository().create();
    }

    @Override
    public void simpleUpdate(float tpf) {
        JmeMain.getGameRepository().update();
    }
}
