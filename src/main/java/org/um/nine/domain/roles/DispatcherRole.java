package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.List;

public class DispatcherRole extends Role {
    public DispatcherRole() {
        super("Dispatcher", ColorRGBA.Magenta);
    }

    @Override
    public RoleAction actions(int key) {
        if (key == 1) {
            return RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN;
        }

        return null;
    }

    @Override
    public RoleEvent events(int key) {
        if (key == 1) {
            return RoleEvent.MOVE_OTHER_PLAYER;
        }

        return null;
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.MOVE_OTHER_PLAYER);

        return events;
    }
}
