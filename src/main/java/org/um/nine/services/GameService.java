package org.um.nine.services;

import com.google.inject.Inject;
import com.jme3.system.AppSettings;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.services.IGameService;

public class GameService implements IGameService {
    @Inject
    private IGameRepository gameRepository;

    @Override
    public void init() {
        this.gameRepository.init();
    }

    @Override
    public void create() {
        this.gameRepository.create();
    }

    @Override
    public void update() {
        this.gameRepository.update();
    }

    @Override
    public Game getApp() {
        return this.gameRepository.getApp();
    }

    @Override
    public AppSettings getSettings() {
        return this.gameRepository.getSettings();
    }

    @Override
    public boolean isStarted() {
        return this.gameRepository.isStarted();
    }

    @Override
    public void setStarted(boolean started) {
        this.gameRepository.setStarted(started);
    }
}
