package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class OperationsExpertRole extends Role {
    static ColorRGBA lightGreen = new ColorRGBA(136f, 255f, 47f, 100f);

    public OperationsExpertRole() {
        super("Operations Expert", lightGreen);
    }

    @Override
    public void actions(String key) {
        /*
         * TODO either build a research station in his current city without discarding
         * (or using) a City card, or once per turn, move from a research station to any
         * city by discarding any City card.
         */

    }

    @Override
    public void events() {
        throw new UnsupportedOperationException("This Role does not have an event");

    }

}
