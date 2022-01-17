package org.um.nine.headless.game.domain;

import com.jme3.math.ColorRGBA;

public enum Color {
    BLACK("Black", ColorRGBA.Black),
    BLUE("Blue", ColorRGBA.Blue),
    BROWN("Brown", ColorRGBA.Brown),
    CYAN("Cyan", ColorRGBA.Cyan),
    GREEN("Green", ColorRGBA.fromRGBA255(56, 119, 0, 1)),
    LIME("Lime", ColorRGBA.fromRGBA255(136, 255, 47, 1)),
    MAGENTA("Magenta", ColorRGBA.Magenta),
    ORANGE("Orange", ColorRGBA.Orange),
    RED("Red", ColorRGBA.Red),
    RED_1("Red 1", ColorRGBA.fromRGBA255(255, 235, 238, 1)),
    RED_2("Red 2", ColorRGBA.fromRGBA255(255, 205, 210, 1)),
    RED_3("Red 3", ColorRGBA.fromRGBA255(239, 154, 154, 1)),
    RED_4("Red 4", ColorRGBA.fromRGBA255(229, 115, 115, 1)),
    RED_5("Red 5", ColorRGBA.fromRGBA255(229, 57, 53, 1)),
    RED_6("Red 6", ColorRGBA.fromRGBA255(211, 47, 47, 1)),
    RED_7("Red 7", ColorRGBA.fromRGBA255(198, 40, 40, 1)),
    RED_8("Red 8", ColorRGBA.fromRGBA255(183, 28, 28, 1)),
    WHITE("White", ColorRGBA.White),
    YELLOW("Yellow", ColorRGBA.Yellow);

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
