package org.um.nine.exceptions;

public class NoActionSelectedException extends Exception {
    public NoActionSelectedException() {
        super("Player did not select an action!");
    }
}
