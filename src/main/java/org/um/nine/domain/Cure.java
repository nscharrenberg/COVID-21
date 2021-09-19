package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

public class Cure {
    private ColorRGBA color;
    private boolean isDiscovered;

    public Cure(ColorRGBA color) {
        this.color = color;
        this.isDiscovered = false;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

    public void setDiscovered(boolean discovered) {
        isDiscovered = discovered;
    }
}
