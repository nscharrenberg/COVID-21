package org.um.nine.contracts.repositories;

import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import org.um.nine.Game;

import java.util.HashMap;

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
}
