package org.um.nine.v1.exceptions;

public class NoActionSelectedException extends Exception {
    public NoActionSelectedException() {
        super("Player did not select an action!");
    }
}
