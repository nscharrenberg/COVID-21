package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.services.IGameService;
import org.um.nine.repositories.local.GameRepository;
import org.um.nine.services.GameService;

public class GuiceConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(IGameRepository.class).to(GameRepository.class);
        bind(IGameService.class).to(GameService.class);
    }
}
