package org.um.nine.v1.contracts.repositories;

import com.jme3.system.AppSettings;
import org.um.nine.v1.Game;

public interface IGameRepository {
    void init();
    void create();
    void update();
    Game getApp();
    AppSettings getSettings();
    boolean isStarted();
    void setStarted(boolean started);
    void start();
    void setApp(Game app);
    int getSpeed();
    void setSpeed(int speed);

    void cleanup();
}
