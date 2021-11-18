package org.um.nine.headless.game.exceptions;

import com.jme3.math.ColorRGBA;
import org.um.nine.headless.game.domain.Color;

public class NoCubesLeftException extends Exception {
    public NoCubesLeftException(Color color) {
        super("No Cubes of color " + color.getName() + " is left.");
    }
}
