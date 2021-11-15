package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class ContingencyPlannerRole extends Role {
    public static String NAME = "Contingency Planner";
    public ContingencyPlannerRole() {
        super(NAME, ColorRGBA.Cyan);
    }

    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.TAKE_ANY_DISCARED_EVENT);
    }

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
