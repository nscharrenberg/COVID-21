package org.um.nine.jme;

import org.um.nine.jme.repositories.GameRepository;
import org.um.nine.jme.repositories.VisualRepository;

public class JmeMain {
    private static GameRepository gameRepository = new GameRepository();
    private static VisualRepository visualRepository = new VisualRepository();

    public static void main(String[] args) {
        gameRepository.setApp(new JmeGame());
        gameRepository.init();
    }

    public static GameRepository getGameRepository() {
        return gameRepository;
    }

    public static VisualRepository getVisualRepository() {
        return visualRepository;
    }
}
