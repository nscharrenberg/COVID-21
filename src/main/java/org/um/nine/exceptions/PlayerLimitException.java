package org.um.nine.exceptions;

public class PlayerLimitException extends Exception {
    public PlayerLimitException() {
        super("The player limit has been reached.");
    }
}
