package org.um.nine.headless.game.domain.roles;

import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher extends Role {
    public Dispatcher() {
        super("Dispatcher", Color.MAGENTA);
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     *
     * @param key
     */
    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN);
    }

    @Override
    public List<RoleAction> actions() {
        ArrayList<RoleAction> events = new ArrayList<>();
        events.add(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN);
        return events;
    }

    /**
     * Perform role events that a user has no power over.
     * e.g. Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it.
     *
     * @param key
     */
    @Override
    public boolean events(RoleEvent key) {
        return key.equals(RoleEvent.MOVE_OTHER_PLAYER);
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.MOVE_OTHER_PLAYER);

        return events;
    }
}
