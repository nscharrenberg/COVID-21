package org.um.nine.headless.game.exceptions;

public class ColorNotFoundException extends Exception{
    public ColorNotFoundException() {
        super("The color does not exist in the game.");
    }

}
