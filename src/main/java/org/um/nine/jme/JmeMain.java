package org.um.nine.jme;

import org.um.nine.jme.repositories.GameRepository;

public class JmeMain {
    private static GameRepository gameRepository = new GameRepository();

    public static void main(String[] args) {
        gameRepository.setApp(new JmeGame());
        gameRepository.init();
    }

    public static GameRepository getGameRepository() {
        return gameRepository;
    }
}
