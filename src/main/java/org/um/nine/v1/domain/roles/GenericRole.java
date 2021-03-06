package org.um.nine.v1.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.v1.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class GenericRole extends Role {
    public GenericRole(String name, ColorRGBA color) {
        super(name, color);
    }

    @Override
    public boolean actions(RoleAction key) {
        return false;
    }

    @Override
    public List<RoleAction> actions() {
        ArrayList<RoleAction> events = new ArrayList<>();
        return events;
    }

    @Override
    public boolean events(RoleEvent key) {
        return false;
    }

    @Override
    public List<RoleEvent> events() {
        return new ArrayList<>();
    }
}
