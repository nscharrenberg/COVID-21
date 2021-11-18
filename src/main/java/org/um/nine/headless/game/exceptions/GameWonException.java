package org.um.nine.headless.game.exceptions;

public class GameWonException extends Exception {
    public GameWonException() {
        super("You win!");
    }
}
