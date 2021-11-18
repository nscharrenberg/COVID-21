package org.um.nine.v1.domain;

import com.jme3.math.ColorRGBA;

public class OutbreakMarker extends Marker {
    private ColorRGBA color;

    public OutbreakMarker(int id, ColorRGBA color) {
        super(id);
        this.color = color;
    }

    public OutbreakMarker(int id, ColorRGBA color, boolean isCurrent) {
        super(id, isCurrent);
        this.color = color;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}
