package org.um.nine.headless.game.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class Researcher extends Role {
    public Researcher() {
        super("Researcher", Color.BROWN);
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     *
     * @param key
     */
    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.GIVE_PLAYER_CITY_CARD);
    }

    @Override
    public List<RoleAction> actions() {
        ArrayList<RoleAction> actions = new ArrayList<>();
        actions.add(RoleAction.GIVE_PLAYER_CITY_CARD);

        return actions;
    }

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     *
     * @param key
     */
    @Override
    public boolean events(RoleEvent key) {
        return false;
    }

    @Override
    public List<RoleEvent> events() {
        return new ArrayList<>();
    }
}
