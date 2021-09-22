package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class QuarantineSpecialistRole extends Role {
    static ColorRGBA darkGreen = new ColorRGBA(56f, 119f, 0f, 100f);

    public QuarantineSpecialistRole() {
        super("Quarantine Specialist", darkGreen);
    }

    @Override
    public void actions(String key) {
        throw new UnsupportedOperationException("This Role does not have an action");
    }

    @Override
    public void events() {
        // TODO: Do not allow disease cubes to be placed
    }
}
