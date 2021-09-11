package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.ISettingRepository;
import org.um.nine.repositories.local.SettingRepository;
import org.um.nine.repositories.local.GameRepository;

public class GuiceConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(IGameRepository.class).to(GameRepository.class);
        bind(ISettingRepository.class).to(SettingRepository.class);
    }
}
