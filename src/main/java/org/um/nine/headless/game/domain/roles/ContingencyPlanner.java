package org.um.nine.headless.game.domain.roles;

import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class ContingencyPlanner extends Role {
    public ContingencyPlanner() {
        super("Contingency Planner", Color.CYAN);
    }

    /**
     * Perform a specific action that a user can take.
     * e.g. Build a research station in the city you are in (no discord needed)
     *
     * @param key
     */
    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.TAKE_ANY_DISCARED_EVENT);
    }

    @Override
    public List<RoleAction> actions() {
        ArrayList<RoleAction> events = new ArrayList<>();
        events.add(RoleAction.TAKE_ANY_DISCARED_EVENT);
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
        return key.equals(RoleEvent.USE_STORED_EVENT_CARD);
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.USE_STORED_EVENT_CARD);

        return events;
    }
}
