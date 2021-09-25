package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class GenericRole extends Role {
    public GenericRole(String name, ColorRGBA color) {
        super(name, color);
    }

    @Override
    public RoleAction actions(int key) {
        return null;
    }

    @Override
    public RoleEvent events(int key) {
        return null;
    }

    @Override
    public List<RoleEvent> events() {
        return new ArrayList<>();
    }
}
