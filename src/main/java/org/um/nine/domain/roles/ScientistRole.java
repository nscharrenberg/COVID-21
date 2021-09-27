package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.List;

public class ScientistRole extends Role {
    public ScientistRole() {
        super("Scientist", ColorRGBA.White);
    }

    @Override
    public boolean actions(RoleAction key) {
        return false;
    }

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
