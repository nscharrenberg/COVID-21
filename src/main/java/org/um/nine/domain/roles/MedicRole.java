package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class MedicRole extends Role {

    public MedicRole() {
        super("Medic", ColorRGBA.Orange);
    }

    @Override
    public void actions(String key) {
        throw new UnsupportedOperationException("This Role does not have an action");

    }

    @Override
    public void events() {
        /*
         * TODO Clear all disease blocks of the same color from a city with clear
         * disease action when no cure is yet discovered if a cure is discoverd clear
         * all disease blocks in the city of that color upon moving in the city,
         * clearing blocks then costs no action.
         */

    }

}
