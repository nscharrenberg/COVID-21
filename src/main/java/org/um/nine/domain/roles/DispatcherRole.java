package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.List;

public class DispatcherRole extends Role {
    public static String NAME = "Dispatcher";
    public DispatcherRole() {
        super(NAME, ColorRGBA.Magenta);
    }

    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN);
    }

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
