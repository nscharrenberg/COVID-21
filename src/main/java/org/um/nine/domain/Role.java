package org.um.nine.domain;

import com.jme3.math.ColorRGBA;

public abstract class Role {
    private String name;
    private ColorRGBA color;

    protected Role(String name, ColorRGBA color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     *
     * @param key - the action to execute
     */
    public abstract void actions(String key);

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     */
    public abstract void events();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}
