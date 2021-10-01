package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.*;
import org.um.nine.repositories.local.*;
import org.um.nine.screens.ConfigurationState;
import org.um.nine.screens.MainMenuState;
import org.um.nine.screens.SettingsState;
import org.um.nine.utils.managers.RenderManager;

public class GuiceConfiguration extends AbstractModule {
    private final IGameRepository gameRepository = new GameRepository();
    private final IBoardRepository boardRepository = new BoardRepository();
    private final ICityRepository cityRepository = new CityRepository();
    private final ICardRepository cardRepository = new CardRepository();
    private final IPlayerRepository playerRepository = new PlayerRepository();
    private final IDiseaseRepository diseaseRepository = new DiseaseRepository();
    private final RenderManager renderManager = new RenderManager();

    // Screens
    private final MainMenuState mainMenuState = new MainMenuState();
    private final SettingsState settingsState = new SettingsState();
    private final ConfigurationState configurationState = new ConfigurationState();

    @Override
    protected void configure() {
        bind(IGameRepository.class).toInstance(gameRepository);
        bind(IBoardRepository.class).toInstance(boardRepository);
        bind(ICityRepository.class).toInstance(cityRepository);
        bind(ICardRepository.class).toInstance(cardRepository);
        bind(IPlayerRepository.class).toInstance(playerRepository);
        bind(IDiseaseRepository.class).toInstance(diseaseRepository);
        bind(RenderManager.class).toInstance(renderManager);

        bind(MainMenuState.class).toInstance(mainMenuState);
        bind(SettingsState.class).toInstance(settingsState);
        bind(ConfigurationState.class).toInstance(configurationState);
    }
}
