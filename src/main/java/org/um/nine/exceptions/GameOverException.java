package org.um.nine.exceptions;

public class GameOverException extends Exception {
    public GameOverException() {
        super("Game Over. You Lost!");
    }
}
