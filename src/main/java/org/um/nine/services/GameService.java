package org.um.nine.services;

import com.google.inject.Inject;
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
}
