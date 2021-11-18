package org.um.nine.v1.domain;

import com.jme3.math.ColorRGBA;
import org.um.nine.v1.domain.roles.RoleAction;
import org.um.nine.v1.domain.roles.RoleEvent;

import java.util.List;

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
     */
    public abstract boolean actions(RoleAction key);

    public abstract List<RoleAction> actions();

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     */
    public abstract boolean events(RoleEvent key);

    public abstract List<RoleEvent> events();

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
