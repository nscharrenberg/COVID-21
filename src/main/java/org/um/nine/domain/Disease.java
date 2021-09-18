package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

public class Disease {
    private ColorRGBA color;

    public Disease(ColorRGBA color) {
        this.color = color;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}
