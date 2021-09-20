package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.*;
import org.um.nine.repositories.local.*;

public class GuiceConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(IGameRepository.class).to(GameRepository.class);
        bind(IBoardRepository.class).to(BoardRepository.class);
        bind(ICityRepository.class).to(CityRepository.class);
        bind(ICardRepository.class).to(CardRepository.class);
        bind(IPlayerRepository.class).to(PlayerRepository.class);
        bind(IDiseaseRepository.class).to(DiseaseRepository.class);
    }
}
