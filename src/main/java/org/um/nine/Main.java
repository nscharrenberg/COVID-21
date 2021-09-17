package org.um.nine;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.um.nine.config.GuiceConfiguration;
import org.um.nine.contracts.repositories.IGameRepository;

import javax.inject.Inject;

public class Main  {
    public static Injector injector = null;

    public static void main(String[] args) {
        injector = Guice.createInjector(new GuiceConfiguration());
        Game game = new Game();
        Main.injector.injectMembers(game);

        IGameRepository gameService = injector.getInstance(IGameRepository.class);
        gameService.init();
    }
}
