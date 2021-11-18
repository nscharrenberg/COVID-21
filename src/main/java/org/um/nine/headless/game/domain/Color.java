package org.um.nine.headless.game.domain;

import com.jme3.math.ColorRGBA;

public enum Color {
    RED("Red", ColorRGBA.Red),
    BLACK("Black", ColorRGBA.Black),
    BLUE("Blue", ColorRGBA.Blue),
    YELLOW("Yellow", ColorRGBA.Yellow),
    CYAN("Cyan", ColorRGBA.Cyan),
    MAGENTA("Magenta", ColorRGBA.Magenta),
    ORANGE("Orange", ColorRGBA.Orange),
    BROWN("Brown", ColorRGBA.Brown),
    WHITE("White", ColorRGBA.White),
    LIME("Lime", ColorRGBA.fromRGBA255(136, 255, 47, 1)),
    GREEN("Green", ColorRGBA.fromRGBA255(56,119,0,1));

    private final String name;
    private final ColorRGBA color;

    Color(String name, ColorRGBA color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ColorRGBA getColor() {
        return color;
    }
}
