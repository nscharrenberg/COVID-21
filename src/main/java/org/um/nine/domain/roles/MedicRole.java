package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class MedicRole extends Role {

    public MedicRole() {
        super("Medic", ColorRGBA.Orange);
    }

    @Override
    public RoleAction actions(int key) {
        return null;
    }

    @Override
    public RoleEvent events(int key) {
        if (key == 1) {
            return RoleEvent.REMOVE_ALL_CUBES_OF_A_COLOR;
        } else if (key == 2) {
            return RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE;
        }

        return null;
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.REMOVE_ALL_CUBES_OF_A_COLOR);
        events.add(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE);

        return events;
    }
}
