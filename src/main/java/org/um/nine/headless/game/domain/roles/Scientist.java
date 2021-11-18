package org.um.nine.headless.game.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class Scientist extends Role {
    public Scientist() {
        super("Scientist", Color.WHITE);
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     *
     * @param key
     */
    @Override
    public boolean actions(RoleAction key) {
        return false;
    }

    @Override
    public List<RoleAction> actions() {
        return new ArrayList<>();
    }

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     *
     * @param key
     */
    @Override
    public boolean events(RoleEvent key) {
        return key.equals(RoleEvent.DISCOVER_CURE_FOUR_CARDS);
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.DISCOVER_CURE_FOUR_CARDS);

        return events;
    }
}
