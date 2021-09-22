package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

public class ResearcherRole extends Role {

    public ResearcherRole() {
        super("Researcher", ColorRGBA.Brown);
    }

    @Override
    public void actions(String key) {
        /*
         * TODO When doing the Share Knowledge action, the Researcher may give any City
         * card from her hand to another player in the same city as her, without this
         * card having to match her city. The transfer must be from her hand to the
         * other player’s hand, but it can occur on either player’s turn.
         */

    }

    @Override
    public void events() {
        throw new UnsupportedOperationException("This Role does not have an event");

    }

}
