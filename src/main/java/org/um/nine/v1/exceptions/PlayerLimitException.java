package org.um.nine.v1.exceptions;

public class PlayerLimitException extends Exception {
    public PlayerLimitException() {
        super("The player limit has been reached.");
    }
}
