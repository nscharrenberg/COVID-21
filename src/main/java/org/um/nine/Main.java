package org.um.nine;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.um.nine.config.GuiceConfiguration;
import org.um.nine.contracts.repositories.IGameRepository;

public class Main  {
    public static final Injector injector = Guice.createInjector(new GuiceConfiguration());

    public static void main(String[] args) {
        IGameRepository gameService = injector.getInstance(IGameRepository.class);
        gameService.init();
    }
}
