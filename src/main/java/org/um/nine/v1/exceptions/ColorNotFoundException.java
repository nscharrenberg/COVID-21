package org.um.nine.v1.exceptions;

public class ColorNotFoundException extends Exception{
    public ColorNotFoundException() {
        super("The color does not exist in the game.");
    }

}
