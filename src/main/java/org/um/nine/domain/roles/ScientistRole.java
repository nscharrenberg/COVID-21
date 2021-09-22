package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

public class ScientistRole extends Role {
    public ScientistRole() {
        super("Scientist", ColorRGBA.White);
    }

    @Override
    public void actions(String key) {
        throw new UnsupportedOperationException("This Role does not have an action");

    }

    @Override
    public void events() {
        /*
         * TODO The Scientist needs only 4 (not 5) City cards of the same disease color
         * to Discover a Cure for that disease.
         */

    }

}
