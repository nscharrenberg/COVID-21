package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class QuarantineSpecialistRole extends Role {
    public QuarantineSpecialistRole() {
        super("Quarantine Specialist", ColorRGBA.Green);
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
