package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.repositories.local.BoardRepository;
import org.um.nine.repositories.local.GameRepository;

public class GuiceConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(IGameRepository.class).to(GameRepository.class);
        bind(IBoardRepository.class).to(BoardRepository.class);
    }
}
