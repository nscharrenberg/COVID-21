package org.um.nine.v1;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.um.nine.v1.config.GuiceConfiguration;
import org.um.nine.v1.contracts.repositories.IGameRepository;

public class Main {
    public static Injector injector = null;

    public static void main(String[] args) {
        injector = Guice.createInjector(new GuiceConfiguration());
        Game game = new Game();
        Main.injector.injectMembers(game);

        IGameRepository gameService = injector.getInstance(IGameRepository.class);
        gameService.init();
    }
}
