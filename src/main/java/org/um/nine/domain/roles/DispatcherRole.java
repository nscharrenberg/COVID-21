package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

public class DispatcherRole extends Role {
    public DispatcherRole() {
        super("Dispatcher", ColorRGBA.Magenta);
    }

    @Override
    public void actions(String key) {
        /*
         * TODO move any pawn, if its owner agrees to any city containing another pawn,
         * or move another player’s pawn if its owner agrees as if it were his own.
         */

    }

    @Override
    public void events() {
        throw new UnsupportedOperationException("This Role does not have an event.");

    }

}
