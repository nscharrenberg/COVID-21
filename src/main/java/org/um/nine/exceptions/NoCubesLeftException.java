package org.um.nine.exceptions;

import com.jme3.math.ColorRGBA;

public class NoCubesLeftException extends Exception {
    public NoCubesLeftException(ColorRGBA color) {
        super("No Cubes of color " + color + " is left.");
    }
}
