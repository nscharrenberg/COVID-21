package org.um.nine.headless.game.exceptions;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;

public class GameOverException extends Exception {
    public GameOverException() {
        super("Game Over. You Lost!");
    }
}
