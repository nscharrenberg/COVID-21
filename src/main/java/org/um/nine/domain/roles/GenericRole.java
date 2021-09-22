package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class GenericRole extends Role {
    public GenericRole(String name, ColorRGBA color) {
        super(name, color);
    }

    @Override
    public void actions(String key) {
        throw new UnsupportedOperationException("This Role does not have an action");
    }

    @Override
    public void events() {
        throw new UnsupportedOperationException("This Role does not have an event");
    }
}
