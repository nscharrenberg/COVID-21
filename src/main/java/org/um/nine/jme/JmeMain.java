package org.um.nine.jme;

import org.um.nine.jme.repositories.GameRepository;
import org.um.nine.jme.repositories.VisualRepository;
import org.um.nine.jme.utils.JmeFactory;
import org.um.nine.v1.Info;

public class JmeMain {
    private static GameRepository gameRepository = new GameRepository();
    private static VisualRepository visualRepository = new VisualRepository();

    public static void main(String[] args) {
        Info.visualize = true;
        JmeFactory.init(gameRepository);
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
