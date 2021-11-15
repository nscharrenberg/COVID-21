package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class QuarantineSpecialistRole extends Role {
    public static String NAME = "Quarantine Specialist";
    public QuarantineSpecialistRole() { //dark green
        super(NAME, ColorRGBA.fromRGBA255(56,119,0,1));
    }

    @Override
    public boolean actions(RoleAction key) {
        return false;
    }

    @Override
    public boolean events(RoleEvent key) {
        return key.equals(RoleEvent.PREVENT_DISEASE_OR_OUTBREAK);
    }

    @Override
    public List<RoleEvent> events() {
        ArrayList<RoleEvent> events = new ArrayList<>();
        events.add(RoleEvent.PREVENT_DISEASE_OR_OUTBREAK);

        return events;
    }
}
