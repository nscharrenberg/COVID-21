package org.um.nine.contracts.repositories;

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
}
