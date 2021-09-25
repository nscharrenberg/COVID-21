package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class ContingencyPlannerRole extends Role {
    public ContingencyPlannerRole() {
        super("Contingency Planner", ColorRGBA.Cyan);
    }

    @Override
    public RoleAction actions(int key) {
        if (key == 1) {
            return RoleAction.TAKE_ANY_DISCARED_EVENT;
        }

        return null;
    }

    @Override
    public RoleEvent events(int key) {
        if (key == 1) {
            return RoleEvent.USE_STORED_EVENT_CARD;
        }

        return null;
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.USE_STORED_EVENT_CARD);

        return events;
    }
}
