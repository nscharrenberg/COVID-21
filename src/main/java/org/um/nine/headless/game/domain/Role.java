package org.um.nine.headless.game.domain;

import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.domain.roles.RoleEvent;

import java.util.List;

public abstract class Role {
    private static int INCREMENT = 0;
    private int id;
    private String name;
    private Color color;

    public Role(String name, Color color) {
        this.id = INCREMENT;
        this.name = name;
        this.color = color;

        INCREMENT++;
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     */
    public abstract boolean actions(RoleAction key);
    public abstract List<org.um.nine.headless.game.domain.roles.RoleAction> actions();

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     */
    public abstract boolean events(RoleEvent key);
    public abstract List<org.um.nine.headless.game.domain.roles.RoleEvent> events();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color.getName() +
                '}';
    }
}
