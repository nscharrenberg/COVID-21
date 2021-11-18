package org.um.nine.headless.game.exceptions;

public class GameOverException extends Exception {
    public GameOverException() {
        super("Game Over. You Lost!");
    }
}
