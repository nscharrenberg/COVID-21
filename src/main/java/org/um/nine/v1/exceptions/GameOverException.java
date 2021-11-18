package org.um.nine.v1.exceptions;

public class GameOverException extends Exception {
    public GameOverException() {
        super("Game Over. You Lost!");
    }
}
